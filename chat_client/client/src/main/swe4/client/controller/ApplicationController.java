package main.swe4.client.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import main.swe4.client.view.ChatClientView;
import main.swe4.client.view.LoginView;
import main.swe4.client.view.RegisterView;
import main.swe4.common.communication.ChatsMessagesHandler;
import main.swe4.common.communication.ServerConnection;
import main.swe4.common.communication.ServerRequestHandler;
import main.swe4.common.database.Database;
import main.swe4.common.datamodel.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ApplicationController {
	LoginView loginView;
	RegisterView registerView;
	ChatClientView chatClientView;
	Database database;
	ServerConnection connection;
	static ChatViewController chatViewController;
	static ChatsMessagesHandler chatsMessagesHandler;
	LoginRegisterController loginRegisterController;
	String serverUrlAndPort;

	public ApplicationController(
			LoginView loginView,
			RegisterView registerView,
			ChatClientView chatClientView,
			String serverUrlAndPort
	) throws RemoteException, MalformedURLException, NotBoundException {
		this.serverUrlAndPort = serverUrlAndPort;

		this.database = (Database) Naming.lookup(serverUrlAndPort);
		this.connection = (ServerConnection) Naming.lookup(serverUrlAndPort);

		this.loginView = loginView;
		this.registerView = registerView;

		this.chatClientView = chatClientView;

		ServerRequestHandler serverRequestHandler = new ServerRequestHandler();
		chatViewController = new ChatViewController(database, connection, chatClientView, serverRequestHandler);
		chatsMessagesHandler = (ChatsMessagesHandler) chatViewController;
		UnicastRemoteObject.exportObject(chatViewController, 0);


		loginRegisterController = new LoginRegisterController(loginView, registerView, database, this);
	}

	public static ChatsMessagesHandler getController() {
		return chatsMessagesHandler;
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
