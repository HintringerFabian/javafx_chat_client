package main.swe4.client.model;

public class Message {
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