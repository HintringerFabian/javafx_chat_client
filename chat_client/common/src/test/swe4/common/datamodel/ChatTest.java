package swe4.common.datamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {
	private Chat chat;
	private User admin;
	private User user1;
	private User user2;
	private Message message1;
	private Message message2;

	@BeforeEach
	void setUp() {
		admin = new User("admin", "admin", "Admin");
		user1 = new User("user1", "user1", "User1");
		user2 = new User("user2", "user2", "User2");
		message1 = new Message(user1, "Hello, user1!");
		message2 = new Message(admin, "Hi, admin!");

		ArrayList<User> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);

		chat = new Chat("Test Chat", admin, users);
	}

	@Test
	void getName() {
		assertEquals("Test Chat", chat.getName());
	}

	@Test
	void getAdmin() {
		assertEquals(admin, chat.getAdmin());
	}

	@Test
	void getMessages() {
		assertEquals(0, chat.getMessages().size());
	}

	@Test
	void addMessage() {
		chat.addMessage(message1);

		assertEquals(1, chat.getMessages().size());
		assertTrue(chat.getMessages().contains(message1));
	}

	@Test
	void getUsers() {
		var users = chat.getUsers();


		assertEquals(3, users.size());
		assertTrue(chat.getUsers().contains(admin));
		assertTrue(chat.getUsers().contains(user1));
		assertTrue(chat.getUsers().contains(user2));
	}

	@Test
	void addUser() {
		var user3 = new User("user3", "user3", "User3");
		chat.addUser(user3);

		assertEquals(4, chat.getUsers().size());
		assertTrue(chat.getUsers().contains(user2));
	}

	@Test
	void getBannedUsers() {
		assertEquals(0, chat.getBannedUsers().size());
	}

	@Test
	void banUser() {
		chat.banUser(user1);

		assertFalse(chat.getUsers().contains(user1));
		assertTrue(chat.getBannedUsers().contains(user1));
	}

	@Test
	void unbanUser() {
		chat.banUser(user1);
		chat.unbanUser(user1);

		assertFalse(chat.getUsers().contains(user1));
		assertFalse(chat.getBannedUsers().contains(user1));
	}

	@Test
	void equals_sameInstance_true() {
		assertEquals(chat, chat);
	}

	@Test
	void equals_null_false() {
		assertNotEquals(null, chat);
	}

	@Test
	void equals_differentClass_false() {
		assertNotEquals("Not a Chat object", chat);
	}

	@Test
	void equals_differentName_false() {
		Chat otherChat = new Chat("Different Chat", admin);
		assertNotEquals(chat, otherChat);
	}

	@Test
	void equals_differentAdmin_false() {
		User differentAdmin = new User("Different Admin", "admin", "Different Admin");
		Chat otherChat = new Chat("Test Chat", differentAdmin);
		assertNotEquals(chat, otherChat);
	}

	@Test
	void equals_sameNameAndAdmin_true() {
		Chat otherChat = new Chat("Test Chat", admin);
		assertEquals(chat, otherChat);
	}
}
