package main.swe4.common;

public interface ServerConnection {
	void registerClient(ServerEventHandler client, User user);
}
