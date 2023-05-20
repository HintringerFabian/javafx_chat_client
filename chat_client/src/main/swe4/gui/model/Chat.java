package main.swe4.gui.model;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class Chat {
	private String name;
	private User admin;
	private Image image;
	private ArrayList<Message> messages = new ArrayList<>();
	private ArrayList<User> users = new ArrayList<>();
	private ArrayList<User> bannedUsers = new ArrayList<>();

	public Chat(String name, User admin, Image image) {
		this.name = name;
		this.admin = admin;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public User getAdmin() {
		return admin;
	}

	public Image getImage() {
		return image;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	public void banUser(User user) {
		bannedUsers.add(user);
		users.remove(user);
	}
}
