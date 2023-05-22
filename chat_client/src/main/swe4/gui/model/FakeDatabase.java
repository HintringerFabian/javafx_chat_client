package main.swe4.gui.model;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FakeDatabase {
	private static FakeDatabase instance;
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<Message> messages = new ArrayList<Message>();
	private final Map<String, Chat> chats = new HashMap<>();

	private FakeDatabase() {
		Image adminImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("../images/user.png")));
		Image userImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("../images/user.png")));

		User admin = new User("Admin user", "admin@adminmail.com", adminImage);
		User user1 = new User("User1", "user1@user.com", userImage);
		User user2 = new User("User2", "user2@user.com,", userImage);

		Chat chat1 = new Chat("Drama Lama", admin, adminImage);
		Chat chat2 = new Chat("The office dudes and dudines", admin, adminImage);
		Chat chat3 = new Chat("Anime Chat", admin, adminImage);

		// TODO: messages have time stamps
		// TODO: messages have a chat reference
		Message message1 = new Message(user1, "Hello", userImage);
		Message message2 = new Message(user2, "Hi there", userImage);
		Message message3 = new Message(user1, "How are you?", userImage);

		users.add(admin);

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

	public Chat getChat(String name) {
		return chats.get(name);
	}
}
