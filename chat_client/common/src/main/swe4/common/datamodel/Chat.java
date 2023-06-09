package swe4.common.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {
	private final String name;
	private final User admin;
	private final ArrayList<Message> messages;
	private final ArrayList<User> users;
	private final ArrayList<User> bannedUsers;

	public Chat(String name, User admin, ArrayList<User> users) {
		this.name = name;
		this.admin = admin;
		this.users = users;

		users.add(admin);

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

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	// TODO check why this is needed
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

	// TODO check why this is needed
	public void unbanUser(User user) {
		bannedUsers.remove(user);
	}

	public ArrayList<User> getBannedUsers() {
		return bannedUsers;
	}

	public void setUsers(ArrayList<User> users) {
		this.users.clear();
		this.users.addAll(users);
	}

	public void setBannedUsers(ArrayList<User> bannedUsers) {
		this.bannedUsers.clear();
		this.bannedUsers.addAll(bannedUsers);
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages.clear();
		this.messages.addAll(messages);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Chat otherChat)) {
			return false;
		}

		return name.equals(otherChat.name) && admin.equals(otherChat.admin);
	}
}
