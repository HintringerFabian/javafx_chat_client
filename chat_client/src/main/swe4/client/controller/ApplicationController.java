package main.swe4.client.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import main.swe4.client.model.Database;
import main.swe4.client.model.FakeDatabase;
import main.swe4.client.model.User;
import main.swe4.client.view.ChatClientView;
import main.swe4.client.view.LoginView;
import main.swe4.client.view.RegisterView;

public class ApplicationController {
	LoginView loginView;
	RegisterView registerView;
	ChatClientView chatClientView;
	Database database;
	ChatViewController chatViewController;
	LoginRegisterController loginRegisterController;

	public ApplicationController(LoginView loginView, RegisterView registerView, ChatClientView chatClientView) {
		this.loginView = loginView;
		this.registerView = registerView;

		this.chatClientView = chatClientView;


		this.database = FakeDatabase.getInstance();

		chatViewController = new ChatViewController(database, chatClientView);
		loginRegisterController = new LoginRegisterController(loginView, registerView, database, this);
	}

	public void run() {
		try {
			registerView.start(new Stage());
			registerView.hide();
			loginView.start(new Stage());
		} catch (Exception e) {
			System.err.println("Error starting login view: " + e.getMessage());
			System.out.println();
			e.printStackTrace();
		}
	}

	public void startLoginRegisterView(LoginView view) {
		Platform.runLater(view::show);
	}

	public void closeView(LoginView view) {
		Platform.runLater(view::hide);
	}

	public void startChatClientView(String username) throws Exception {
		User user = database.getUser(username);
		chatViewController.setUser(user);

		chatClientView.start(new Stage());
	}
}
