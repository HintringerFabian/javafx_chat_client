package swe4.common.communication;

import swe4.common.database.Database;

import java.io.Serializable;
import java.rmi.Remote;

public interface ChatServer extends Database, ServerConnection, Remote, Serializable {
}
