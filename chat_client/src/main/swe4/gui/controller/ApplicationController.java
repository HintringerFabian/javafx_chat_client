package main.swe4.gui.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.swe4.gui.model.User;
import main.swe4.gui.view.ChatClientView;
import main.swe4.gui.view.LoginView;
import main.swe4.gui.view.RegisterView;

public class ApplicationController {
	// TODO: in next ue add chat controller and login/register controller
	LoginView loginView;
	RegisterView registerView;
	ChatClientView chatClientView;

	public ApplicationController(LoginView loginView, RegisterView registerView, ChatClientView chatClientView) {
		this.loginView = loginView;
		this.registerView = registerView;
		this.chatClientView = chatClientView;
	}

	public void run() {
		try {
			loginView.setLeftButtonAction(() -> switchToView(registerView, loginView));
			loginView.setRightButtonAction(this::handleLogin);

			registerView.setLeftButtonAction(() -> switchToView(loginView, registerView));
			registerView.setRightButtonAction(this::handleRegister);

			registerView.start(new Stage());
			registerView.hide();

			loginView.start(new Stage());
		} catch (Exception e) {
			System.err.println("Error starting login view: " + e.getMessage());
		}
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
						Image profilePicture = new Image(getClass().getResourceAsStream("../css/profilePic.png"));
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
