package main.swe4.client.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LoginView extends Application {

	Button leftButton;
	Button rightButton;
	TextField usernameField;
	PasswordField passwordField;
	BorderPane loginScreen;
	Stage loginStage;

	int sceneWidth;
	int sceneHeight;
	String title;
	String leftButtonText;
	String rightButtonText;

	public LoginView() {
		title = "Login";
		leftButtonText = "Go to Register";
		rightButtonText = "Login";
		sceneHeight = 200;
		sceneWidth = 325;
		loginScreen = createLoginScreen();
	}

	public void hide() {
		loginStage.hide();
	}

	public void show() {
		loginStage.show();
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.loginStage = stage;
		loginStage.setTitle("Chat Client");
		loginStage.setResizable(false);

		// Create the scene
		Scene scene = new Scene(loginScreen, sceneWidth, sceneHeight);

		// Add CSS stylesheet
		try {
			scene.getStylesheets().add(getClass().getResource("../css/login.css").toExternalForm());
		} catch (Exception e) {
			System.out.println("Error loading stylesheet: " + e.getMessage());
		}

		loginStage.setScene(scene);
		loginStage.show();
	}

	BorderPane createLoginScreen() {
		// Create UI elements
		Label titleLabel = new Label(title);
		usernameField = new TextField();
		passwordField = new PasswordField();

		// create buttons
		leftButton = new Button(leftButtonText);
		rightButton = new Button(rightButtonText);

		// Set IDs for UI elements
		titleLabel.setId("titleLabel");

		usernameField.setId("usernameField");
		passwordField.setId("passwordField");
		leftButton.setId("leftButton");
		rightButton.setId("rightButton");

		// Create form layout
		GridPane formLayout = createFormLayout();

		// Create button layout
		HBox buttonLayout = new HBox(10);
		buttonLayout.setAlignment(Pos.CENTER);
		buttonLayout.getChildren().addAll(leftButton, rightButton);

		// Create container layout
		BorderPane containerLayout = new BorderPane();
		containerLayout.setPadding(new Insets(20));
		containerLayout.setTop(titleLabel);
		containerLayout.setCenter(formLayout);
		containerLayout.setBottom(buttonLayout);

		return containerLayout;
	}

	GridPane createFormLayout() {
		Label usernameLabel = new Label("Username:");
		Label passwordLabel = new Label("Password:");
		usernameLabel.setId("usernameLabel");
		passwordLabel.setId("passwordLabel");

		GridPane formLayout = new GridPane();

		formLayout.setHgap(10);
		formLayout.setVgap(10);
		formLayout.addRow(0, usernameLabel, usernameField);
		formLayout.addRow(1, passwordLabel, passwordField);
		return formLayout;
	}

	public void setLeftButtonAction(Runnable action) {
		leftButton.setOnAction(event -> action.run());
	}

	public void setRightButtonAction(Runnable action) {
		rightButton.setOnAction(event -> action.run());
	}

	public String getUsername() {
		return usernameField.getText();
	}

	public String getPassword() {
		return passwordField.getText();
	}
}
