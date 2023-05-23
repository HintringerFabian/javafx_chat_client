package main.swe4.gui.model;

import javafx.scene.image.Image;

public class Message {
	private User user;
	private String message;

	public Message(User username, String message) {
		this.user = username;
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}
}