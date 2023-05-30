package swe4.client;

import javafx.application.Application;
import javafx.stage.Stage;
import swe4.client.controller.ApplicationController;
import swe4.client.view.ChatClientView;
import swe4.client.view.LoginView;
import swe4.client.view.RegisterView;

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

		var port = Registry.REGISTRY_PORT;
		var host = "localhost";
		var serverUrlAndPort = "rmi://" + host + ":" + port + "/ChatServer";

		ApplicationController applicationController = new ApplicationController(loginView, registerView, chatClientView, serverUrlAndPort);

		applicationController.run();
	}
}
