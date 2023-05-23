package main.swe4.gui.model;

import java.util.Map;

public interface Database {
	Chat getChat(String name);
	Map<String, Chat> getChats();
	void addChat(Chat chat);
	void removeChat(Chat chat);
}
