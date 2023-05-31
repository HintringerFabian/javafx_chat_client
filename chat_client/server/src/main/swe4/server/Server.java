package swe4.server;

import swe4.common.communication.ChatServer;
import swe4.common.database.Database;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {

	public static void main(String[] args) throws RemoteException, MalformedURLException, SQLException {

		var port = Registry.REGISTRY_PORT;
		var host = "localhost";
		var serverUrlAndPort = "rmi://" + host + ":" + port + "/ChatServer";

		var CONNECTION_STRING = "jdbc:mysql://localhost/ChatClient?autoReconnect=true&useSSL=false";
		var USER_NAME = "root";

		System.setProperty("java.rmi.server.hostname", host);

		Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER_NAME, null);

		Database database = new MySqlDatabase(connection);

		ChatServer server = new ChatDao(database);
		var remoteDatabase = UnicastRemoteObject.exportObject(server, port);

		LocateRegistry.createRegistry(port);
		Naming.rebind(serverUrlAndPort, remoteDatabase);

		System.out.println("Server started at: " + serverUrlAndPort);
	}
}