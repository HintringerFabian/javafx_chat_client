package swe4.common.database;

import swe4.common.datamodel.Chat;
import swe4.common.datamodel.Message;
import swe4.common.datamodel.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public interface Database {
	Map<String, Chat> getChats();

	ArrayList<User> getUsers();

	User getUser(int id);

	void addChat(Chat chat);

	void removeChat(Chat chat);

	void addMessage(Chat chat, Message message);

	void banUser(Chat chat, User user) throws SQLException;

	void unbanUser(Chat chat, User user);

	void addUser(Chat chat, User user) throws SQLException;

	void register(String username, String password, String fullName) throws SQLException;
}
