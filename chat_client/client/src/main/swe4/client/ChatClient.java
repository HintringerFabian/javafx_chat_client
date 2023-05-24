package main.swe4.client;

import javafx.application.Application;
import javafx.stage.Stage;
import main.swe4.client.view.LoginView;
import main.swe4.client.view.RegisterView;
import main.swe4.client.controller.ApplicationController;
import main.swe4.client.view.ChatClientView;

public class ChatClient extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStages) throws Exception {
		LoginView loginView = new LoginView();
		RegisterView registerView = new RegisterView();
		ChatClientView chatClientView = new ChatClientView();

		ApplicationController applicationController =
				new ApplicationController(loginView, registerView, chatClientView);

		applicationController.run();
	}
}
