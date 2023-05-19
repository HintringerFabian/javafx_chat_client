package main.swe4.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import main.swe4.gui.controller.ApplicationController;
import main.swe4.gui.view.ChatClientView;
import main.swe4.gui.view.LoginView;
import main.swe4.gui.view.RegisterView;

public class ChatClient extends Application {

	public static void main(String[] args) {
		launch(args);
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
