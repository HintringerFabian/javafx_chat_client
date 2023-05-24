package main.swe4.gui.model;

import javafx.scene.image.Image;

public class User {
	private String username;
	//private String password;
	private String fullName;

	public User(String username, String email) {
		this.username = username;
		//this.password = password;
		this.fullName = email;
	}

	public String getUsername() {
		return username;
	}

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
