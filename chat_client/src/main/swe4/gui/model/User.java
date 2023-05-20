package main.swe4.gui.model;

import javafx.scene.image.Image;

public class User {
	private String username;
	//private String password;
	private String email;
	private Image picture;

	public User(String username, String email, Image picture) {
		this.username = username;
		//this.password = password;
		this.email = email;
		this.picture = picture;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public Image getPicture() {
		return picture;
	}
}
