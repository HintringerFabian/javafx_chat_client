package swe4.common.datamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
	private User user1;
	private User user2;

	@BeforeEach
	void setUp() {
		user1 = new User("user1", "user1@example.com");
		user2 = new User("user2", "user2@example.com");
	}

	@Test
	void getUsername() {
		assertEquals("user1", user1.getUsername());
		assertEquals("user2", user2.getUsername());
	}

	@Test
	void getFullName() {
		assertEquals("user1@example.com", user1.getFullName());
		assertEquals("user2@example.com", user2.getFullName());
	}

	@Test
	void equals_sameInstance_true() {
		assertTrue(user1.equals(user1));
	}

	@Test
	void equals_null_false() {
		assertFalse(user1.equals(null));
	}

	@Test
	void equals_differentClass_false() {
		assertFalse(user1.equals("Not a User object"));
	}

	@Test
	void equals_differentUsername_false() {
		User otherUser = new User("user2", "user1@example.com");
		assertFalse(user1.equals(otherUser));
	}

	@Test
	void equals_differentFullName_false() {
		User otherUser = new User("user1", "user2@example.com");
		assertFalse(user1.equals(otherUser));
	}

	@Test
	void equals_sameUsernameAndFullName_true() {
		User otherUser = new User("user1", "user1@example.com");
		assertTrue(user1.equals(otherUser));
	}

	@Test
	void hashCode_equalObjects_equalHashCodes() {
		User otherUser = new User("user1", "user1@example.com");
		assertEquals(user1.hashCode(), otherUser.hashCode());
	}

	@Test
	void hashCode_differentUsername_differentHashCodes() {
		User otherUser = new User("user2", "user1@example.com");
		assertNotEquals(user1.hashCode(), otherUser.hashCode());
	}

	@Test
	void hashCode_differentFullName_differentHashCodes() {
		User otherUser = new User("user1", "user2@example.com");
		assertNotEquals(user1.hashCode(), otherUser.hashCode());
	}
}
