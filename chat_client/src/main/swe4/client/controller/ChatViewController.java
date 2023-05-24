package main.swe4.client.controller;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import main.swe4.client.model.Chat;
import main.swe4.client.model.Database;
import main.swe4.client.model.Message;
import main.swe4.client.model.User;
import main.swe4.client.view.ChatClientView;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ChatViewController implements EventListener {

	private final Database database;
	private final ChatClientView view;
	private User currentUser;

	ChatViewController(Database database, ChatClientView view) {
		this.database = database;
		this.view = view;

		view.setEventListener(this);

		view.setMessageSearchAction(this::messageSearchAction);
		view.setLensButtonAction(this::lensButtonAction);
		view.setChatPaneClickEvent(this::chatPaneClickEvent);
		view.setCreateChatNameValidation(this::newChatNameValidation);
		view.setCreateChatButtonAction(this::createChatButtonAction);
		view.setSendButtonAction(this::sendButtonAction);
	}

	public void setUser(User user) {
		this.currentUser = user;

		view.setUser(user);

		var userChats = database.getChatsFor(user);
		view.setChats(userChats);
	}

	@Override
	public void handleDeleteChat(Chat chat) {
		if (isCurrentUserAdmin(chat)) {
			deleteChat(chat);
		} else {
			view.showToast("You are not an admin");
		}
	}

	@Override
	public void handleBanUser(Chat chat, String username) {
		if (isCurrentUserAdmin(chat)) {
			var user = database.getUser(username);

			if (user == null) {
				view.showToast("User does not exist");
			} else if (user.equals(currentUser)) {
				view.showToast("You cannot ban yourself");
			} else if (chat.getBannedUsers().contains(user)) {
				view.showToast("User already banned");
			} else {
				chat.banUser(user);
				view.showToast("User banned");
			}
		} else {
			view.showToast("You are not an admin");
		}
	}

	@Override
	public void handleUnbanUser(Chat chat, String username) {
		if (isCurrentUserAdmin(chat)) {
			var user = database.getUser(username);

			if (user == null) {
				view.showToast("User does not exist");
			} else if (!chat.getBannedUsers().contains(user)) {
				view.showToast("User not banned");
			} else {
				chat.unbanUser(user);
				view.showToast("User unbanned");
			}
		} else {
			view.showToast("You are not an admin");
		}
	}

	private void messageSearchAction(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		var currentChatName = view.getCurrentChatName();
		var currentChat = database.getChat(currentChatName);
		var messages = currentChat.getMessages();

		var filteredMessages = messages.stream()
				.filter(message -> message.getMessage().contains(newValue))
				.collect(Collectors.toCollection(ArrayList::new));

		updateMessages(filteredMessages);
	}

	private void lensButtonAction(ActionEvent event) {
		var headerPaneItems = view.getUserPane().getChildren();
		var searchField = view.getSearchField();

		if (headerPaneItems.contains(searchField)) {
			searchField.clear();
			headerPaneItems.remove(searchField);
		} else {
			headerPaneItems.add(searchField);
		}
	}

	private void chatPaneClickEvent(ObservableValue<? extends Chat> obs, Chat oldValue, Chat newValue) {
		if (newValue != null && !newValue.equals(oldValue)) {
			var chat = database.getChat(newValue.getName());
			var chatName = chat.getName();

			var newChatHeaderPane = view.createChatHeaderPane(chatName);

			view.setUserPane(newChatHeaderPane);
			view.updateChatHeaderPane(newChatHeaderPane);

			updateMessages(chat.getMessages());

			view.setCurrentChatName(chatName);

			System.out.println("Selected chat: " + chatName);
		}
	}

	private void newChatNameValidation(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		var empty = newValue.trim().isEmpty();

		var chatCreateButton = view.getChatCreateButtonNode();

		chatCreateButton.setDisable(empty);
	}

	private void createChatButtonAction() {
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
				newChat.addUser(currentUser);
				view.showToast("Chat already exists, you will be added to it");
			}
		} else {
			newChat = new Chat(chatName, currentUser);
			newChat.addUser(currentUser);

			database.addChat(newChat);
		}

		chatPane.getItems().add(newChat);

		createChatNameField.clear();
		createChatButton.setDisable(true);
	}

	private void sendButtonAction() {
		var messageField = view.getMessageField();
		var message = messageField.getText();
		var chat = database.getChat(view.getCurrentChatName());

		if (message.isEmpty()) {
			view.showToast("Message cannot be empty");
		} else if (chat == null) {
			view.showToast("No chat selected");
		} else {
			var newMessage = new Message(currentUser, message);
			chat.addMessage(newMessage);

			messageField.clear();
			updateMessages(chat.getMessages());
		}
	}

	private boolean isCurrentUserAdmin(Chat chat) {
		var chatAdmin = chat.getAdmin();

		return currentUser.equals(chatAdmin);
	}

	private void deleteChat(Chat chat) {
		System.out.println("Deleting chat: " + chat.getName());
		database.removeChat(chat);

		var chats = database.getChats();
		var chatPane = view.getChatPane();

		// update the chat selection pane
		chatPane.getItems().clear();
		chatPane.getItems().addAll(chats.values());
	}

	private void updateMessages(ArrayList<Message> messages) {
		var chatArea = view.getChatArea();

		chatArea.getItems().clear();
		chatArea.getItems().addAll(messages);
	}
}
