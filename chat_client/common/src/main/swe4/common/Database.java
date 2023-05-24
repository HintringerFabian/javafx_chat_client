package main.swe4.common;

import java.util.ArrayList;
import java.util.Map;

public interface Database {
	Database getInstance();
	Chat getChat(String name);
	Map<String, Chat> getChats();
	ArrayList<Chat> getChatsFor(User user);
	void addChat(Chat chat);
	void removeChat(Chat chat);
	User getUser(String username);
}
