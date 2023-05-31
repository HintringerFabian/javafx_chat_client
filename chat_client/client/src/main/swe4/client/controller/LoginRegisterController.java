package swe4.client.controller;

import swe4.client.view.LoginView;
import swe4.client.view.RegisterView;
import swe4.common.database.DatabaseService;

public class LoginRegisterController {
	// needed for login and registration views when functionality is implemented
	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private final DatabaseService database;
	private final LoginView loginView;
	private final RegisterView registerView;
	private final ApplicationController mainController;

	public LoginRegisterController(LoginView loginView, RegisterView registerView, DatabaseService database, ApplicationController mainController) {
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
		boolean verified = false;
		try {
			verified = database.loginUser(loginView.getUsername(), loginView.getPassword());
		} catch (Exception e) {
			loginView.showToast("There was an error logging in: Please try again.");
		}
		handleVerification(verified, loginView);
	}

	private void handleRegister() {
		boolean verified = false;
		try {
			verified = database.registerUser(loginView.getUsername(), loginView.getPassword(), registerView.getFullName());
		} catch (Exception e) {
			registerView.showToast("There was an error registering: Please try again.");
		}

		handleVerification(verified, registerView);
	}

	public void handleVerification(boolean verified, LoginView view) {

		if(!verified) {
			var notification = (view == loginView) ? "Login failed: Username or Password incorrect" : "Registration failed: User already exists";
			view.showToast(notification);
			return;
		}

		// when login is successful, show chat client view
		//mainController.switchToView(chatClientView, loginView);
		mainController.closeView(view);
		try {
			var userName = view.getUsername();
			mainController.startChatClientView(userName);
		} catch (Exception e) {
			System.out.println("Error starting chat client view: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
