package swe4.client.view;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ToastNotifier {
	private final Stage stage;

	public ToastNotifier(Stage stage) {
		this.stage = stage;
	}

	public void showToast(String message) {
		// Create a label for the toast message
		Label toastLabel = new Label(message);
		toastLabel.getStyleClass().add("toast-label");

		// Create a stack pane to hold the toast label
		StackPane toastPane = new StackPane(toastLabel);
		toastPane.getStyleClass().add("toast-pane");

		// Create a popup to show the toast
		Popup toastPopup = new Popup();
		toastPopup.getContent().add(toastPane);

		// Configure fade-in and fade-out animations
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), toastPane);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setOnFinished(event -> toastPopup.hide());

		FadeTransition fadeIn = new FadeTransition(Duration.millis(500), toastPane);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);

		PauseTransition pause = new PauseTransition(Duration.seconds(2));
		pause.setOnFinished(event -> fadeOut.play());

		// Show the toast
		toastPopup.show(stage);//, toastX, toastY);

		// Start fade-in animation
		fadeIn.play();
		pause.play();
	}
}
