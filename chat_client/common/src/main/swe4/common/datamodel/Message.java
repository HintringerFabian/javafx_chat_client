package swe4.common.datamodel;

import java.io.Serializable;

import static swe4.common.datamodel.Utils.hashString;

public class Message implements Serializable {
	// TODO maybe next time dont forget to implement timestamps
	// TODO also add them to the view
	// TODO 26.05.2023 i forgot to add timestamps
	// TODO 30.05.2023 guess what i forgot again
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

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Message otherMessage)) {
			return false;
		}

		return this.user.equals(otherMessage.user) && this.message.equals(otherMessage.message);
	}

	@Override
	public int hashCode() {
		return hashString(user.getUsername()) + hashString(message);
	}
}