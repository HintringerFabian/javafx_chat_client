package main.swe4.client.controller;

import main.swe4.common.Chat;

import java.rmi.RemoteException;

public interface EventListener {
	void handleDeleteChat(Chat chat);
	void handleBanUser(Chat chat, String username);
	void handleUnbanUser(Chat chat, String username);
}
