package main.swe4.gui.model;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FakeDatabase implements Database {
	private static FakeDatabase instance;
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<Message> messages = new ArrayList<Message>();
	private final Map<String, Chat> chats = new HashMap<>();

	private FakeDatabase() {
		Image adminImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("../images/user.png")));
		Image userImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("../images/user.png")));

		User admin = new User("Admin", "admin@adminmail.com", adminImage);
		User user1 = new User("Fabian", "user1@user.com", userImage);
		User user2 = new User("Dom", "user2@user.com,", userImage);

		var userlist = new ArrayList<User>();
		userlist.add(user1);
		userlist.add(user2);
		userlist.add(admin);

		Chat chat1 = new Chat("Drama Lama", admin, adminImage, userlist);
		Chat chat2 = new Chat("The office dudes and dudines", admin, adminImage, userlist);
		Chat chat3 = new Chat("Anime Chat", admin, adminImage, userlist);

		// TODO: messages have time stamps
		// TODO: messages have a chat reference
		Message message1 = new Message(user1, "Hello", userImage);
		Message message2 = new Message(user2, "Hi there", userImage);
		Message message3 = new Message(user1, "How are you?", userImage);

		users.add(admin);
		users.add(user1);
		users.add(user2);

		messages.add(message1);
		messages.add(message2);
		messages.add(message3);

		chats.put(chat1.getName(), chat1);
		chats.put(chat2.getName(), chat2);
		chats.put(chat3.getName(), chat3);

		chats.get(chat1.getName()).addMessage(message1);
		chats.get(chat1.getName()).addMessage(message2);
		chats.get(chat1.getName()).addMessage(message3);

		chats.get(chat2.getName()).addMessage(message1);
		chats.get(chat2.getName()).addMessage(message2);
	}

	public static FakeDatabase getInstance() {
		if (instance == null) {
			instance = new FakeDatabase();
		}
		return instance;
	}

	@Override
	public Chat getChat(String name) {
		return chats.get(name);
	}

	@Override
	public Map<String, Chat> getChats() {
		return chats;
	}

	@Override
	public ArrayList<Chat> getChatsFor(User user) {
		var chatsWithUser = chats.values()
				.stream()
				.filter(chat -> chat.getUsers().contains(user))
				.toList();

		return new ArrayList<>(chatsWithUser);
	}

	@Override
	public void addChat(Chat chat) {

	}

	@Override
	public void removeChat(Chat chat) {
		chats.remove(chat.getName());
	}

	@Override
	public User getUser(String username) {
		return users.stream()
				.filter(user -> user.getUsername().equals(username))
				.findFirst()
				.orElse(null);
	}
}
