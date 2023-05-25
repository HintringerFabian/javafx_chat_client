package main.swe4.common;

import java.io.Serializable;

public class Message implements Serializable {
	// TODO maybe next time dont forget to implement timestamps
	// TODO also add them to the view
	// TODO 26.05.2023 i forgot to add timestamps
	private final User user;
	private final String message;

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