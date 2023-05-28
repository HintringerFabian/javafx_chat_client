package main.swe4.common.communication;

import main.swe4.client.controller.ApplicationController;
import main.swe4.common.datamodel.Chat;
import main.swe4.common.datamodel.Message;

import java.io.Serializable;
import java.rmi.RemoteException;

public class ServerRequestHandler implements ServerEventHandler, Serializable {

	private ChatsMessagesHandler controller() {
		var controller = ApplicationController.getController();

		return (ChatsMessagesHandler) controller;
	}

	@Override
	public void hasConnection() {
		// needs to exist for the server to be able to call the method
	}

	@Override
	public void handleNewChatFromServer(Chat chat) throws RemoteException {
		var controller = controller();
		controller.handleNewChatFromServer(chat);
	}

	@Override
	public void handleNewMessageFromServer(Chat chat, Message message) throws RemoteException {
		var controller = controller();
		controller.handleNewMessageFromServer(chat, message);
	}
}
