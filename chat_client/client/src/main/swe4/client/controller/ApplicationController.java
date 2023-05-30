package swe4.client.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import swe4.client.view.ChatClientView;
import swe4.client.view.LoginView;
import swe4.client.view.RegisterView;
import swe4.common.communication.ServerConnection;
import swe4.common.database.Database;
import swe4.common.datamodel.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ApplicationController {
	private static ChatViewController chatViewController;
	private final LoginView loginView;
	private final RegisterView registerView;
	private final ChatClientView chatClientView;
	private final Database database;

	public ApplicationController(
			LoginView loginView,
			RegisterView registerView,
			ChatClientView chatClientView,
			String serverUrlAndPort
	) throws RemoteException, MalformedURLException, NotBoundException {

		this.database = (Database) Naming.lookup(serverUrlAndPort);
		ServerConnection connection = (ServerConnection) Naming.lookup(serverUrlAndPort);

		this.loginView = loginView;
		this.registerView = registerView;

		this.chatClientView = chatClientView;

		ServerRequestHandler serverRequestHandler = new ServerRequestHandler();
		chatViewController = new ChatViewController(database, connection, chatClientView, serverRequestHandler);
		serverRequestHandler.setController(chatViewController);
		UnicastRemoteObject.exportObject(serverRequestHandler, 0);

		LoginRegisterController loginRegisterController = new LoginRegisterController(loginView, registerView, database, this);
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
