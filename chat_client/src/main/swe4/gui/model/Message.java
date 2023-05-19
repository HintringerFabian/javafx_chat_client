package main.swe4.gui.model;

import javafx.scene.image.Image;

public class Message {
	private String username;
	private String message;
	private Image picture;

	public Message(String username, String message, Image picture) {
		this.username = username;
		this.message = message;

		// TODO: What did i do here?
		// Get the picture from the user and set it to the message
		// Will fix this later
		this.picture = picture;
	}

	public String getUsername() {
		return username;
	}

	public String getMessage() {
		return message;
	}

	public Image getPicture() {
		return picture;
	}
}