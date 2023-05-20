package main.swe4.gui.model;

import javafx.scene.image.Image;

public class Message {
	private User user;
	private String message;
	private Image picture;

	public Message(User username, String message, Image picture) {
		this.user = username;
		this.message = message;

		// TODO: What did i do here?
		// Get the picture from the user and set it to the message
		// Will fix this later
		this.picture = picture;
	}

	public User getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}

	public Image getPicture() {
		return picture;
	}
}