package main.swe4.server;

import main.swe4.common.*;
import main.swe4.database.FakeDatabase;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements ServerConnection {
	private final ConcurrentHashMap<User, ServerEventHandler> clients = new ConcurrentHashMap<>();

	public static void main(String[] args) throws RemoteException, MalformedURLException {

		// TODO think of logic:
		// - how many clients are connected?
		// - which ports are used?
		// - how to handle multiple clients?

		var port = Registry.REGISTRY_PORT;
		var host = "localhost";
		var serverUrlAndPort = "rmi://" + host + ":" + port + "/ChatServer";

		System.setProperty("java.rmi.server.hostname", host);

		Database database = FakeDatabase.getInstance();
		var remoteDatabase = UnicastRemoteObject.exportObject(database, port);

		LocateRegistry.createRegistry(port);
		Naming.rebind(serverUrlAndPort, remoteDatabase);

		System.out.println("Server started at: " + serverUrlAndPort);

		// Start a new thread to check if the clients are still connected
		new Thread(() -> new Server().checkConnection()).start();
	}

	@Override
	public void registerClient(ServerEventHandler client, User user) {
		clients.put(user, client);
	}


	public void sendMessageToClients(Chat chat, Message message) {
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

	void handleNewChatFromServer(Chat chat) {
		// TODO it is late i do not know what i am doing anymore, think of what this method should do
		// i mean we create a new chat? or is one user added to the chat? what happens here?
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

	private void checkConnection() {
		while (true) {
			var clientIterator = clients.entrySet().iterator();
			while (clientIterator.hasNext()) {
				var client = clientIterator.next().getValue();

				tryHandle(client::hasConnection, clientIterator, client);
			}

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void tryHandle(
			ClientAction action,
			Iterator<Map.Entry<User, ServerEventHandler>> clientIter,
			ServerEventHandler client
	) {
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
				clientIter.remove();
			}
		}
	}
}
