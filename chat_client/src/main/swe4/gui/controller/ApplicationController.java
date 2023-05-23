package main.swe4.gui.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.swe4.gui.model.Chat;
import main.swe4.gui.model.FakeDatabase;
import main.swe4.gui.model.Message;
import main.swe4.gui.model.User;
import main.swe4.gui.view.ChatClientView;
import main.swe4.gui.view.LoginView;
import main.swe4.gui.view.RegisterView;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ApplicationController {
	// TODO: in next ue add chat controller and login/register controller
	LoginView loginView;
	RegisterView registerView;
	ChatClientView chatClientView;
	FakeDatabase fakeDatabase;

	public ApplicationController(LoginView loginView, RegisterView registerView, ChatClientView chatClientView) {
		this.loginView = loginView;
		this.registerView = registerView;
		this.chatClientView = chatClientView;
		this.fakeDatabase = FakeDatabase.getInstance();
	}

	public void run() {
		try {
			loginView.setLeftButtonAction(() -> switchToView(registerView, loginView));
			loginView.setRightButtonAction(this::handleLogin);

			registerView.setLeftButtonAction(() -> switchToView(loginView, registerView));
			registerView.setRightButtonAction(this::handleRegister);

			registerView.start(new Stage());
			registerView.hide();

			chatClientView.setMessageSearchAction(this::messageSearchAction);
			chatClientView.setLensButtonAction(this::lensButtonAction);
			chatClientView.setChatPaneClickEvent(this::chatPaneClickEvent);

			loginView.start(new Stage());
		} catch (Exception e) {
			System.err.println("Error starting login view: " + e.getMessage());
		}
	}

	private void chatPaneClickEvent(ObservableValue<? extends Chat> obs, Chat oldValue, Chat newValue) {
		if (newValue != null && !newValue.equals(oldValue)) {
			var chat = fakeDatabase.getChat(newValue.getName());
			var chatName = chat.getName();

			var newChatHeaderPane = chatClientView.createChatHeaderPane(chat.getImage(), chatName);

			chatClientView.setUserPane(newChatHeaderPane);
			chatClientView.updateChatHeaderPane(newChatHeaderPane);

			updateMessages(chat.getMessages());

			chatClientView.setCurrentChatName(chatName);

			System.out.println("Selected chat: " + chatName);
		}
	}

	// TODO (optional): split to multiple controllers
	private void messageSearchAction(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		var currentChatName = chatClientView.getCurrentChatName();
		var currentChat = fakeDatabase.getChat(currentChatName);
		var messages = currentChat.getMessages();

		var filteredMessages = messages.stream()
			.filter(message -> message.getMessage().contains(newValue))
			.collect(Collectors.toCollection(ArrayList::new));

		updateMessages(filteredMessages);
	}

	private void lensButtonAction(ActionEvent event) {
		var headerPaneItems = chatClientView.getUserPane().getChildren();
		var searchField = chatClientView.getSearchField();

		if (headerPaneItems.contains(searchField)) {
			searchField.clear();
			headerPaneItems.remove(searchField);
		} else {
			headerPaneItems.add(searchField);
		}
	}

	private void updateMessages(ArrayList<Message> messages) {
		var chatArea = chatClientView.getChatArea();

		chatArea.getItems().clear();
		chatArea.getItems().addAll(messages);
	}

	private void handleLogin() {
		// TODO: Implement login functionality

		// when login is successful, show chat client view
		switchToView(chatClientView, loginView);
	}

	private void handleRegister() {
		// TODO: Implement register functionality

		// when register is successful, show chat client view
		switchToView(chatClientView, registerView);
	}

	private void switchToView(Application startApp, LoginView closeApp) {
		Platform.runLater(() -> {
			try {
				if (!(startApp instanceof LoginView loginView)) {

					if (startApp instanceof ChatClientView chatClientView) {
						String username = closeApp.getUsername();
						Image profilePicture = new Image(getClass().getResourceAsStream("../images/profilePic.png"));
						User user = new User(username, username + "@chat.com", profilePicture);
						chatClientView.setUser(user);
					}

					startApp.start(new Stage());
				} else {
					loginView.show();
				}
			} catch (Exception e) {
				System.err.println("Error starting login view: " + e.getMessage());
			}
		});

		closeApp.hide();
	}
}
