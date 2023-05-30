package swe4.client.controller;


import swe4.common.datamodel.Chat;

import java.io.Serializable;

public interface ViewEventHandler extends Serializable {
	void handleDeleteChatInView(Chat chat);

	void handleBanUserInView(Chat chat, String username);

	void handleUnbanUserInView(Chat chat, String username);
}
