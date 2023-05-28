package main.swe4.common.datamodel;

import java.io.Serializable;

public class User implements Serializable {
	private final String username;
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

		return this.fullName.equals(otherUser.fullName);
	}
}
