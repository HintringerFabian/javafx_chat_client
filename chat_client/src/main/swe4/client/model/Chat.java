package main.swe4.client.model;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class Chat {
	private String name;
	private User admin;
	private Image image;
	private ArrayList<Message> messages;
	private ArrayList<User> users;
	private ArrayList<User> bannedUsers;

	public Chat(String name, User admin, ArrayList<User> users) {
		this.name = name;
		this.admin = admin;
		this.users = users;

		messages = new ArrayList<>();
		bannedUsers = new ArrayList<>();
	}

	public Chat(String name, User admin) {
		this(name, admin, new ArrayList<>());
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

	public ArrayList<User> getUsers() {
		return users;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public void unbanUser(User user) {
		bannedUsers.remove(user);
	}

	public ArrayList<User> getBannedUsers() {
		return bannedUsers;
	}
}
