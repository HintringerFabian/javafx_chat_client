package swe4.common.datamodel;

import java.io.Serializable;
import java.sql.Timestamp;

import static swe4.common.Utils.hashString;

public class Message implements Serializable {
	private final User user;
	private final String message;
	//Timestamp datatype is compatible with SQL
	private final Timestamp timestamp;

	public Message(User username, String message) {
		this.user = username;
		this.message = message;
		this.timestamp = new Timestamp(System.currentTimeMillis());
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

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getTimestampString(String format) {
		return timestamp.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern(format));
	}
}