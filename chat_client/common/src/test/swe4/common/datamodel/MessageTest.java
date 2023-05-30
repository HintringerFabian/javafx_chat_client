package swe4.common.datamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
	private User user;
	private String message;
	private Message msg;

	@BeforeEach
	void setUp() {
		user = new User("user1", "user1@example.com");
		message = "Hello, user1!";
		msg = new Message(user, message);
	}

	@Test
	void getUser() {
		assertEquals(user, msg.getUser());
	}

	@Test
	void getMessage() {
		assertEquals(message, msg.getMessage());
	}

	@Test
	void equals_sameInstance_true() {
		assertEquals(msg, msg);
	}

	@Test
	void equals_null_false() {
		assertNotEquals(null, msg);
	}

	@Test
	void equals_differentClass_false() {
		assertNotEquals("Not a Message object", msg);
	}

	@Test
	void equals_differentUser_false() {
		User differentUser = new User("user2", "user2@example.com");
		Message otherMsg = new Message(differentUser, message);
		assertNotEquals(msg, otherMsg);
	}

	@Test
	void equals_differentMessage_false() {
		String differentMessage = "Hi, user1!";
		Message otherMsg = new Message(user, differentMessage);
		assertNotEquals(msg, otherMsg);
	}

	@Test
	void equals_sameUserAndMessage_true() {
		Message otherMsg = new Message(user, message);
		assertEquals(msg, otherMsg);
	}

	@Test
	void hashCode_equalObjects_equalHashCodes() {
		Message otherMsg = new Message(user, message);
		assertEquals(msg.hashCode(), otherMsg.hashCode());
	}

	@Test
	void hashCode_differentUser_differentHashCodes() {
		User differentUser = new User("user2", "user2@example.com");
		Message otherMsg = new Message(differentUser, message);
		assertNotEquals(msg.hashCode(), otherMsg.hashCode());
	}

	@Test
	void hashCode_differentMessage_differentHashCodes() {
		String differentMessage = "Hi, user1!";
		Message otherMsg = new Message(user, differentMessage);
		assertNotEquals(msg.hashCode(), otherMsg.hashCode());
	}
}
