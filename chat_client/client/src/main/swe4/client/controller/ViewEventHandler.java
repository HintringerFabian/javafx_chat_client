package main.swe4.client.controller;

import main.swe4.common.Chat;

public interface ViewEventHandler {
	void handleDeleteChatInView(Chat chat);
	void handleBanUserInView(Chat chat, String username);
	void handleUnbanUserInView(Chat chat, String username);
}
