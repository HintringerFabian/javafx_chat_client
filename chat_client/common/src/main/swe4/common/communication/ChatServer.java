package swe4.common.communication;

import swe4.common.database.DatabaseService;

import java.io.Serializable;
import java.rmi.Remote;

public interface ChatServer extends DatabaseService, ServerConnection, Remote, Serializable {
}
