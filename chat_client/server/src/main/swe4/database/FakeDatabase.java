package swe4.database;

import swe4.common.datamodel.Chat;
import swe4.common.datamodel.Message;
import swe4.common.datamodel.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FakeDatabase {
	private static FakeDatabase instance;
	private final ArrayList<User> users = new ArrayList<>();
	private final Map<String, Chat> chats = new HashMap<>();

	private FakeDatabase() {
		User admin = new User("Admin", "admin@adminmail.com");
		User user1 = new User("Fabian", "user1@user.com");
		User user2 = new User("Dom", "user2@user.com,");

		var userlist = new ArrayList<User>();
		userlist.add(user1);
		userlist.add(user2);
		userlist.add(admin);

		Chat chat1 = new Chat("Drama Lama", admin, userlist);
		Chat chat2 = new Chat("The office dudes and dudines", admin, userlist);
		Chat chat3 = new Chat("Anime Chat", admin, userlist);
		Chat secretChat = new Chat("Secrets", admin);

		// TODO: messages have time stamps
		Message message1 = new Message(user1, "Hello");
		Message message2 = new Message(user2, "Hi there");
		Message message3 = new Message(user1, "How are you?");
		Message secretMessage = new Message(admin, "This is a secret message");

		users.add(admin);
		users.add(user1);
		users.add(user2);

		chats.put(chat1.getName(), chat1);
		chats.put(chat2.getName(), chat2);
		chats.put(chat3.getName(), chat3);
		chats.put(secretChat.getName(), secretChat);

		chats.get(chat1.getName()).addMessage(message1);
		chats.get(chat1.getName()).addMessage(message2);
		chats.get(chat1.getName()).addMessage(message3);

		chats.get(chat2.getName()).addMessage(message1);
		chats.get(chat2.getName()).addMessage(message2);

		chats.get(secretChat.getName()).addMessage(secretMessage);
	}

	public static FakeDatabase getInstance() {
		if (instance == null) {
			instance = new FakeDatabase();
		}
		return instance;
	}

	public Map<String, Chat> getChats() {
		return chats;
	}

	public ArrayList<User> getUsers() {
		return users;
	}
}
