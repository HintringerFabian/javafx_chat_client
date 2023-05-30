package swe4.server;

import swe4.common.Action;
import swe4.common.communication.ChatServer;
import swe4.common.communication.ServerEventHandler;
import swe4.common.database.Database;
import swe4.common.datamodel.Chat;
import swe4.common.datamodel.Message;
import swe4.common.datamodel.User;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatDao implements ChatServer {
	private final ConcurrentHashMap<User, ServerEventHandler> clients = new ConcurrentHashMap<>();
	private final Database database;

	public ChatDao(Database database) {
		this.database = database;

		// start a thread that checks if the clients are still connected
		new Thread(this::checkConnection).start();
	}

	@Override
	public void registerClient(ServerEventHandler client, User user) {
		clients.put(user, client);
	}

	public void sendNewMessage(Chat chat, Message message) {
		// extract the users from the chat
		var users = chat.getUsers();

		var clientIterator = clients.entrySet().iterator();

		// send every user the message -> client can be found in the clients map
		while (clientIterator.hasNext()) {
			var iter = clientIterator.next();
			var client = iter.getValue();

			// check if the user is in the chat
			if (users.contains(iter.getKey())) {
				tryHandle(() -> client.handleNewMessageFromServer(chat, message), clientIterator, client);
			}
		}
	}

	public void sendNewChat(Chat chat) {
		var users = chat.getUsers();

		var clientIterator = clients.entrySet().iterator();

		// send every user the message -> client can be found in the clients map
		while (clientIterator.hasNext()) {
			var iter = clientIterator.next();
			var client = iter.getValue();

			// check if the user is in the chat
			if (users.contains(iter.getKey())) {
				tryHandle(() -> client.handleNewChatFromServer(chat), clientIterator, client);
			}
		}
	}

	public void sendNewChat(Chat chat, User user) {
		var client = clients.get(user);
		tryHandle(() -> client.handleNewChatFromServer(chat), null, client);
	}

	private void checkConnection() {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(() -> {
			var clientIterator = clients.entrySet().iterator();
			while (clientIterator.hasNext()) {
				var client = clientIterator.next().getValue();
				tryHandle(client::hasConnection, clientIterator, client);
			}
		}, 0, 3, TimeUnit.SECONDS);
	}

	@Override
	public Chat getChat(String name) throws RemoteException {
		var chats = database.getChats();
		var chat = chats.get(name);
		return chat;
	}

	@Override
	public Map<String, Chat> getChats() throws RemoteException {
		var chats = database.getChats();
		return chats;
	}

	@Override
	public ArrayList<Chat> getChatsFor(User user) throws RemoteException {
		var chats = database.getChats();

		var chatsWithUser = chats.values()
				.stream()
				.filter(chat -> chat.getUsers().contains(user))
				.toList();

		return new ArrayList<>(chatsWithUser);
	}

	@Override
	public void addChat(Chat chat) throws RemoteException {
		var chats = database.getChats();
		chats.put(chat.getName(), chat);

		sendNewChat(chat);
	}

	@Override
	public void removeChat(Chat chat) throws RemoteException {
		var chats = database.getChats();
		chats.remove(chat.getName());
	}

	@Override
	public void addMessage(Chat chat, Message message) throws RemoteException {
		// TODO there is a bug where a banned user can still send messages
		var chats = database.getChats();
		var dbChat = chats.get(chat.getName());

		if (dbChat != null) {
			dbChat.addMessage(message);
		}

		sendNewMessage(chat, message);
	}

	@Override
	public User getUser(String username) throws RemoteException {
		var users = database.getUsers();
		var user = users.stream()
				.filter(u -> u.getUsername().equals(username))
				.findFirst()
				.orElse(null);
		return user;
	}

	@Override
	public void banUser(Chat chat, User user) throws RemoteException {
		var dbChat = database.getChats().get(chat.getName());

		if (dbChat != null) {
			var users = dbChat.getUsers();
			var bannedUsers = dbChat.getBannedUsers();

			users.remove(user);
			bannedUsers.add(user);
		}
	}

	@Override
	public void unbanUser(Chat chat, User user) throws RemoteException {
		var dbChat = database.getChats().get(chat.getName());

		if (dbChat != null) {
			var bannedUsers = dbChat.getBannedUsers();
			bannedUsers.remove(user);
		}
	}

	@Override
	public void addUser(Chat chat, User user) throws RemoteException {
		var dbChat = database.getChats().get(chat.getName());

		if (dbChat != null) {
			var users = dbChat.getUsers();
			users.add(user);

			sendNewChat(chat, user);
		}
	}

	private void tryHandle(Action action, Iterator<Map.Entry<User, ServerEventHandler>> clientIter, ServerEventHandler client) {
		try {
			// we want to perform an action on the client
			action.perform();
		} catch (RemoteException e) {
			e.printStackTrace();
			try {
				// as we got an exception, we assume that the client is not connected anymore
				// lets test it
				client.hasConnection();
			} catch (RemoteException ex) {
				// remove the client from the list, as it is not connected anymore, we tried twice
				if (clientIter != null) clientIter.remove();
			}
		}
	}
}
