package swe4.server;

import swe4.common.database.Database;
import swe4.common.datamodel.Chat;
import swe4.common.datamodel.Message;
import swe4.common.datamodel.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MySqlDatabase implements Database {

	private final Connection connection;

	public MySqlDatabase(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Map<String, Chat> getChats() {
		try (var statement = connection.prepareStatement("SELECT * FROM chat")) {
			var resultSet = statement.executeQuery();

			var map = new HashMap<String, Chat>();

			// create a new chat for every row in the result set
			while (resultSet.next()) {
				var name = resultSet.getString("name");
				var admin = getUser(resultSet.getInt("admin_id"));
				var users = getUsersForChat(name);

				var messages = getMessagesForChat(name);
				var bannedUsers = getBannedUsersForChat(name);

				var chat = new Chat(name, admin, users);
				chat.setMessages(messages);
				chat.setBannedUsers(bannedUsers);

				map.put(name, chat);

			}

			return map;


		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private ArrayList<User> getBannedUsersForChat(String name) {
		try (
				var statement = connection.prepareStatement(
				"SELECT * " +
						"FROM chat_banned_user " +
						"INNER JOIN user ON chat_banned_user.user_id = user.id " +
						"WHERE chat_id = ?")
		) {

			statement.setString(1, name);
			var resultSet = statement.executeQuery();

			return getUsersFrom(resultSet);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private ArrayList<Message> getMessagesForChat(String name) {
		try (
				var statement = connection.prepareStatement(
						"SELECT * " +
								"FROM chat_message " +
								"INNER JOIN message ON chat_message.message_id = message.id " +
								"INNER JOIN user ON message.user_id = user.id " +
								"WHERE chat_name = ?"
				)
		) {
			statement.setString(1, name);
			var resultSet = statement.executeQuery();

			var messages = new ArrayList<Message>();

			// create a new message for every row in the result set
			while (resultSet.next()) {
				var user = new User(
						resultSet.getString("username"),
						resultSet.getString("password"),
						resultSet.getString("fullName")
				);

				var message = new Message(
						user,
						resultSet.getString("message")
				);

				messages.add(message);
			}

			return messages;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private ArrayList<User> getUsersForChat(String name) {
		try (
				var statement = connection.prepareStatement(
						"SELECT * " +
								"FROM chat_user " +
								"INNER JOIN user ON chat_user.user_id = user.id " +
								"WHERE chat_id = ?"
				)
		) {
			statement.setString(1, name);
			var resultSet = statement.executeQuery();

			return getUsersFrom(resultSet);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public ArrayList<User> getUsers() {
		try (var statement = connection.prepareStatement("SELECT * FROM user")) {
			var resultSet = statement.executeQuery();

			return getUsersFrom(resultSet);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public User getUser(int id) {
		try (var statement = connection.prepareStatement("SELECT * FROM user WHERE id = ?")) {
			statement.setInt(1, id);
			var resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return new User(
						resultSet.getString("username"),
						resultSet.getString("password"),
						resultSet.getString("fullName")
				);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addChat(Chat chat) {
		try (var statement = connection.prepareStatement("INSERT INTO chat (name, admin_id) VALUES (?, ?)")) {
			statement.setString(1, chat.getName());
			statement.setInt(2, getUserId(chat.getAdmin()));

			statement.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeChat(Chat chat) {
		try {
			try (var statement = connection.prepareStatement("DELETE FROM chat WHERE name = ?")) {
				statement.setString(1, chat.getName());
				statement.executeUpdate();
			}

			try (var statement = connection.prepareStatement("DELETE FROM chat_message WHERE chat_name = ?")) {
				// delete all messages from the chat
				statement.setString(1, chat.getName());
				statement.executeUpdate();
			}

			try (var statement = connection.prepareStatement("DELETE FROM chat_user WHERE chat_id = ?")) {
				// delete all users from the chat
				statement.setString(1, chat.getName());
				statement.executeUpdate();
			}

			try (var statement = connection.prepareStatement("DELETE FROM chat_banned_user WHERE chat_id = ?")) {
				// delete all banned users from the chat
				statement.setString(1, chat.getName());
				statement.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addMessage(Chat chat, Message message) {
		try (
				var statement = connection.prepareStatement(
						"INSERT INTO message (user_id, message, timestamp) VALUES (?, ?, ?)")
		) {
			statement.setInt(1, getUserId(message.getUser()));
			statement.setString(2, message.getMessage());
			statement.setTimestamp(3, message.getTimestamp());
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		try (
				var statement = connection.prepareStatement(
						"INSERT INTO chat_message (chat_name, message_id) VALUES (?, ?)")
		) {
			statement.setString(1, chat.getName());
			statement.setInt(2, getMessageId(message));
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void banUser(Chat chat, User user) throws SQLException {
		var statement = connection.prepareStatement("INSERT INTO chat_banned_user (chat_id, user_id) VALUES (?, ?)");
		statement.setString(1, chat.getName());
		statement.setInt(2, getUserId(user));
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public void unbanUser(Chat chat, User user) {
		try (
				var statement = connection.prepareStatement(
						"DELETE FROM chat_banned_user WHERE chat_id = ? AND user_id = ?"
				)
		) {
			statement.setString(1, chat.getName());
			statement.setInt(2, getUserId(user));
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addUser(Chat chat, User user) throws SQLException {
		var statement = connection.prepareStatement("INSERT INTO chat_user (chat_id, user_id) VALUES (?, ?)");
		statement.setString(1, chat.getName());
		statement.setInt(2, getUserId(user));
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public void register(String username, String password, String fullName) throws SQLException {
		var statement = connection.prepareStatement("INSERT INTO user (username, password, fullName) VALUES (?, ?, ?)");
		statement.setString(1, username);
		statement.setString(2, password);
		statement.setString(3, fullName);
		statement.executeUpdate();
		statement.close();
	}

	private int getMessageId(Message message) {
		try (var statement = connection.prepareStatement("SELECT id FROM message WHERE message = ?")) {
			statement.setString(1, message.getMessage());
			var resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getInt("id");
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private int getUserId(User admin) {
		try (var statement = connection.prepareStatement("SELECT id FROM user WHERE username = ?")) {
			statement.setString(1, admin.getUsername());
			var resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getInt("id");
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private ArrayList<User> getUsersFrom(ResultSet resultSet) {
		try {
			ArrayList<User> users = new ArrayList<>();

			// create a new user for every row in the result set
			while (resultSet.next()) {
				var user = new User(
						resultSet.getString("username"),
						resultSet.getString("password"),
						resultSet.getString("fullName")
				);

				users.add(user);
			}

			return users;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
