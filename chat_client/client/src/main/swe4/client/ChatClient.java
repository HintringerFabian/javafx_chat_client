package main.swe4.client;

import javafx.application.Application;
import javafx.stage.Stage;
import main.swe4.client.controller.ApplicationController;
import main.swe4.client.view.ChatClientView;
import main.swe4.client.view.LoginView;
import main.swe4.client.view.RegisterView;

import java.rmi.registry.Registry;

public class ChatClient extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStages) throws Exception {

		LoginView loginView = new LoginView();
		RegisterView registerView = new RegisterView();
		ChatClientView chatClientView = new ChatClientView();

		// TODO add a window which showcases all new system messages - like a log
		// - new chat created
		// - new user joined
		// - user banned
		// - user left
		// - user unbanned

		var port = Registry.REGISTRY_PORT;
		var host = "localhost";
		var serverUrlAndPort = "rmi://" + host + ":" + port + "/ChatServer";

		ApplicationController applicationController = new ApplicationController(loginView, registerView, chatClientView, serverUrlAndPort);

		applicationController.run();
	}
}
