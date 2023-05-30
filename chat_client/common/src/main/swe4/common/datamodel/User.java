package swe4.common.datamodel;

import java.io.Serializable;

public class User implements Serializable {
	private final String username;
	//private String password;
	private final String fullName;

	public User(String username, String email) {
		this.username = username;
		//this.password = password;
		this.fullName = email;
	}

	public String getUsername() {
		return username;
	}

	@SuppressWarnings("unused")
	public String getFullName() {
		return fullName;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof User otherUser)) {
			return false;
		}

		return this.fullName.equals(otherUser.fullName) && this.username.equals(otherUser.username);
	}

	@Override
	public int hashCode() {
		return hashString(fullName) + hashString(username);
	}

	private int hashString(String str) {
		// Prime numbers for hashing
		int[] primes = { 31, 37, 41, 43, 47, 53, 59 };

		int hash = 1;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			hash = hash * primes[i % primes.length] + ch;
		}
		return hash;
	}
}
