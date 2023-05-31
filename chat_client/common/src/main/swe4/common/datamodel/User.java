package swe4.common.datamodel;

import java.io.Serializable;

import static swe4.common.Utils.hashString;

public class User implements Serializable {
	private final String username;
	private final String fullName;
	private final String password;

	public User(String username, String password, String fullName) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
	}

	public User(String username, String password) {
		this(username, password, "");
	}

	public String getUsername() {
		return username;
	}

	@SuppressWarnings("unused")
	public String getFullName() {
		return fullName;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof User otherUser)) {
			return false;
		}

		return this.password.equals(otherUser.password) && this.username.equals(otherUser.username);
	}

	@Override
	public int hashCode() {
		return hashString(password) + hashString(username);
	}
}
