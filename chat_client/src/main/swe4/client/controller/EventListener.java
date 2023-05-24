package main.swe4.client.controller;

import main.swe4.client.model.Chat;

public interface EventListener {
	void handleDeleteChat(Chat chat);
	void handleBanUser(Chat chat, String username);
	void handleUnbanUser(Chat chat, String username);
}