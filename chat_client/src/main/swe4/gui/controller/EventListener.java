package main.swe4.gui.controller;

import main.swe4.gui.model.Chat;

public interface EventListener {
	void handleDeleteChat(Chat chat);
	void handleUnbanUser(Chat chat);
	void handleBanUser(Chat chat);
}
