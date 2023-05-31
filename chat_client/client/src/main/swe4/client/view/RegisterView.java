package swe4.client.view;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RegisterView extends LoginView {

	@SuppressWarnings("FieldCanBeLocal")
	private TextField nameField;

	public RegisterView() {
		title = "Register";
		leftButtonText = "Go to Login";
		rightButtonText = "Register";
		sceneHeight = 250;
		sceneWidth = 325;
		loginScreen = createLoginScreen();
	}

	@Override
	public void start(Stage stage) throws Exception {
		super.start(stage);
	}

	@Override
	GridPane createFormLayout() {
		GridPane formLayout = super.createFormLayout();

		Label nameLabel = new Label("Full Name:");
		nameLabel.setId("nameLabel");
		nameField = new TextField();
		nameField.setId("nameField");

		// Move existing fields to row 1 and 2
		ObservableList<Node> children = formLayout.getChildren();
		Node usernameLabel = children.get(0);
		Node usernameField = children.get(1);
		Node passwordLabel = children.get(2);
		Node passwordField = children.get(3);
		children.removeAll(usernameLabel, passwordLabel, usernameField, passwordField);

		// Add new fields at row 0
		formLayout.addRow(0, nameLabel, nameField);
		formLayout.addRow(1, usernameLabel, usernameField);
		formLayout.addRow(2, passwordLabel, passwordField);

		return formLayout;
	}

	public String getFullName() {
		return nameField.getText();
	}
}
