package swe4.client.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import swe4.client.view.ChatClientView;
import swe4.common.Action;
import swe4.common.communication.ServerConnection;
import swe4.common.communication.ServerEventHandler;
import swe4.common.database.DatabaseService;
import swe4.common.datamodel.Chat;
import swe4.common.datamodel.Message;
import swe4.common.datamodel.User;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ChatViewController implements ViewEventHandler, Serializable {

	private final DatabaseService database;
	private final ServerConnection connection;
	private final ServerEventHandler serverRequestHandler;
	private transient final ChatClientView view;
	ArrayList<Chat> chats;
	private User currentUser;

	ChatViewController(DatabaseService database, ServerConnection connection, ChatClientView view, ServerEventHandler serverEventHandler) {
		this.database = database;
		this.connection = connection;
		this.view = view;
		this.serverRequestHandler = serverEventHandler;

		view.setEventHandler(this);

		view.setMessageSearchAction(this::messageSearchAction);
		view.setLensButtonAction(this::lensButtonAction);
		view.setChatPaneClickEvent(this::chatPaneClickEvent);
		view.setCreateChatNameValidation(this::newChatNameValidation);
		view.setCreateChatButtonAction(this::createChatButtonAction);
		view.setSendButtonAction(this::sendButtonAction);
	}

	public void setUser(User user) {
		this.currentUser = user;

		view.setUser(user.getUsername());

		tryWithConnection(() -> {
			chats = (ArrayList<Chat>) database.getChatsFor(user);
			view.setChats(chats);
			view.updateChats();
		});

		tryWithConnection(() -> {
			connection.registerClient(serverRequestHandler, user);
		});
	}

	public void handleNewChatFromServer(Chat chat) {
		chats.add(chat);
		Platform.runLater(() -> {
			view.setChats(chats);
			view.updateChats();
		});
	}

	public void handleNewMessageFromServer(Chat chat, Message message) {
		var index = chats.indexOf(chat);
		var chatToUpdate = chats.get(index);
		chatToUpdate.addMessage(message);

		var chatName = chatToUpdate.getName();
		var selectedChat = view.getCurrentChatName();
		if (chatName.equals(selectedChat)) {
			var messages = chatToUpdate.getMessages();
			updateMessages(messages);
		}
	}

	@Override
	public void handleDeleteChatInView(Chat chat) {
		if (isCurrentUserAdmin(chat)) {
			deleteChat(chat);
		} else {
			view.showToast("You are not an admin");
		}
	}

	@Override
	public void handleBanUserInView(Chat chat, String username) {
		if (isCurrentUserAdmin(chat)) {

			try {
				var user = database.getUser(username);

				if (user == null) {
					view.showToast("User does not exist");
				} else if (user.equals(currentUser)) {
					view.showToast("You cannot ban yourself");
				} else if (chat.getBannedUsers().contains(user)) {
					view.showToast("User already banned");
				} else {

					tryWithConnection(() -> {
						database.banUser(chat, user);
						view.showToast("User banned");
					});

					database.banUser(chat, user);
					view.showToast("User banned");
				}
			} catch (RemoteException remoteException) {
				remoteException.printStackTrace();
				throw new RuntimeException(remoteException);

			}
		} else {
			view.showToast("You are not an admin");
		}
	}

	@Override
	public void handleUnbanUserInView(Chat chat, String username) {
		if (isCurrentUserAdmin(chat)) {
			try {
				var user = database.getUser(username);

				if (user == null) {
					view.showToast("User does not exist");
				} else if (!chat.getBannedUsers().contains(user)) {
					view.showToast("User not banned");
				} else {
					database.unbanUser(chat, user);
					view.showToast("User unbanned");
				}
			} catch (RemoteException remoteException) {
				remoteException.printStackTrace();
				throw new RuntimeException(remoteException);
			}

		} else {
			view.showToast("You are not an admin");
		}
	}

	private void messageSearchAction(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		tryWithConnection(() -> {
			var currentChatName = view.getCurrentChatName();
			var currentChat = database.getChat(currentChatName);
			var messages = currentChat.getMessages();

			var filteredMessages = messages.stream()
					.filter(message -> message.getMessage().contains(newValue))
					.collect(Collectors.toCollection(ArrayList::new));

			updateMessages(filteredMessages);
		});
	}

	private void lensButtonAction(ActionEvent event) {
		var headerPaneItems = view.getUserPane().getChildren();
		var searchField = view.getSearchField();

		if (headerPaneItems.contains(searchField)) {
			searchField.clear();
			headerPaneItems.remove(searchField);
		} else {
			headerPaneItems.add(searchField);
			Platform.runLater(searchField::requestFocus);
		}
	}

	private void chatPaneClickEvent(ObservableValue<? extends Chat> obs, Chat oldValue, Chat newValue) {
		if (newValue != null && !newValue.equals(oldValue)) {
			tryWithConnection(() -> {
				var chat = database.getChat(newValue.getName());
				var chatName = chat.getName();

				var newChatHeaderPane = view.createChatHeaderPane(chatName);

				view.setUserPane(newChatHeaderPane);
				view.updateChatHeaderPane(newChatHeaderPane);

				updateMessages(chat.getMessages());

				view.setCurrentChatName(chatName);

				System.out.println("Selected chat: " + chatName);
			});
		}
	}

	private void newChatNameValidation(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		var empty = newValue.trim().isEmpty();

		var chatCreateButton = view.getChatCreateButtonNode();

		chatCreateButton.setDisable(empty);
	}

	private void createChatButtonAction() {
		tryWithConnection(() -> {
			var createChatNameField = view.getCreateChatNameField();
			var createChatButton = view.getChatCreateButtonNode();
			var chatPane = view.getChatPane();
			var chats = database.getChats();
			var chatName = createChatNameField.getText();

			Chat newChat;
			if (chats.containsKey(chatName)) {
				newChat = chats.get(chatName);

				if (newChat.getBannedUsers().contains(currentUser)) {
					view.showToast("You are banned from this chat");
				} else {
					database.addUser(newChat, currentUser);
					view.showToast("Chat already exists, you will be added to it");
				}
			} else {
				newChat = new Chat(chatName, currentUser);
				newChat.addUser(currentUser);

				database.addChat(newChat);
			}

			createChatNameField.clear();
			createChatButton.setDisable(true);
		});
	}

	private void sendButtonAction() {
		tryWithConnection(() -> {
			var messageField = view.getMessageField();
			var message = messageField.getText();
			var chat = database.getChat(view.getCurrentChatName());

			if (message.isEmpty()) {
				view.showToast("Message cannot be empty");
			} else if (chat == null) {
				view.showToast("No chat selected");
			} else {
				var newMessage = new Message(currentUser, message);
				tryWithConnection(() -> {
					database.addMessage(chat, newMessage);
					messageField.clear();
				});
			}
		});
	}

	private boolean isCurrentUserAdmin(Chat chat) {
		var chatAdmin = chat.getAdmin();

		return currentUser.equals(chatAdmin);
	}

	private void deleteChat(Chat chat) {
		tryWithConnection(() -> {
			database.removeChat(chat);

			var chats = database.getChats();
			var chatPane = view.getChatPane();

			// update the chat selection pane
			chatPane.getItems().clear();
			chatPane.getItems().addAll(chats.values());

			System.out.println("Deleting chat: " + chat.getName());
		});
	}

	private void updateMessages(ArrayList<Message> messages) {
		var chatArea = view.getChatArea();

		Platform.runLater(() -> {
			chatArea.getItems().clear();
			chatArea.getItems().addAll(messages);
		});
	}

	private void tryWithConnection(Action action) {
		try {
			action.perform();
		} catch (RemoteException remoteException) {
			remoteException.printStackTrace();
			Platform.runLater(() -> {
				view.showToast("Database error, please try to restart application");
			});
		}
	}

	public void handleNotificationFromServer(String notification) {
		Platform.runLater(() -> {
			view.showToast(notification);
		});
	}
}
