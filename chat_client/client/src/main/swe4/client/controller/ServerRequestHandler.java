package swe4.client.controller;

import swe4.common.communication.ServerEventHandler;
import swe4.common.datamodel.Chat;
import swe4.common.datamodel.Message;

import java.io.Serializable;
import java.rmi.RemoteException;

public class ServerRequestHandler implements ServerEventHandler, Serializable {
	private transient ChatViewController controller;

	public void setController(ChatViewController controller) {
		this.controller = controller;
	}

	@Override
	public void hasConnection() {
		// needs to exist for the server to be able to call the method
	}

	@Override
	public void handleNewChatFromServer(Chat chat) throws RemoteException {
		controller.handleNewChatFromServer(chat);
	}

	@Override
	public void handleNewMessageFromServer(Chat chat, Message message) throws RemoteException {
		controller.handleNewMessageFromServer(chat, message);
	}
}
