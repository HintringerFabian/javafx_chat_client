package swe4.common.datamodel;

import java.io.Serializable;

import static swe4.common.Utils.hashString;

public class User implements Serializable {
	private final String username;
	// TODO support passwords
	//private String password;
	private final String fullName;

	public User(String username, String email) {
		this.username = username;
		//this.password = password;
		this.fullName = email;
	}

	public String getUsername() {
		return username;
	}

	@SuppressWarnings("unused")
	public String getFullName() {
		return fullName;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof User otherUser)) {
			return false;
		}

		return this.fullName.equals(otherUser.fullName) && this.username.equals(otherUser.username);
	}

	@Override
	public int hashCode() {
		return hashString(fullName) + hashString(username);
	}
}
