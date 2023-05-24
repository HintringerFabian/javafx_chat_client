package main.swe4.gui.model;

import java.util.ArrayList;
import java.util.Map;

public interface Database {
	Chat getChat(String name);
	Map<String, Chat> getChats();
	ArrayList<Chat> getChatsFor(User user);
	void addChat(Chat chat);
	void removeChat(Chat chat);
	User getUser(String username);
}
