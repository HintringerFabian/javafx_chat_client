package main.swe4.client.controller;

import main.swe4.client.model.Database;
import main.swe4.client.view.LoginView;
import main.swe4.client.view.RegisterView;

public class LoginRegisterController {
	// needed for login and registration views when functionality is implemented
	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private final Database database;
	private final LoginView loginView;
	private final RegisterView registerView;
	private final ApplicationController mainController;

	public LoginRegisterController(LoginView loginView, RegisterView registerView, Database database, ApplicationController mainController) {
		this.loginView = loginView;
		this.registerView = registerView;
		this.database = database;
		this.mainController = mainController;

		loginView.setLeftButtonAction(() -> {
			mainController.closeView(loginView);
			mainController.startLoginRegisterView(registerView);
		});

		registerView.setLeftButtonAction(() -> {
			mainController.closeView(registerView);
			mainController.startLoginRegisterView(loginView);
		});

		loginView.setRightButtonAction(this::handleLogin);
		registerView.setRightButtonAction(this::handleRegister);
	}

	private void handleLogin() {
		// TODO: Implement login functionality

		// when login is successful, show chat client view
		//mainController.switchToView(chatClientView, loginView);
		mainController.closeView(loginView);
		try {
			var userName = loginView.getUsername();
			mainController.startChatClientView(userName);
		} catch (Exception e) {
			System.out.println("Error starting chat client view: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void handleRegister() {
		// TODO: Implement register functionality

		// when register is successful, show chat client view
		//mainController.switchToView(chatClientView, registerView);
		mainController.closeView(registerView);
		try {
			var userName = registerView.getUsername();
			mainController.startChatClientView(userName);
		} catch (Exception e) {
			System.out.println("Error starting chat client view: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
