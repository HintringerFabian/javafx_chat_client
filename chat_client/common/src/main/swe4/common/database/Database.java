package swe4.common.database;

import swe4.common.datamodel.Chat;
import swe4.common.datamodel.User;

import java.util.ArrayList;
import java.util.Map;

public interface Database {
	Map<String, Chat> getChats();
	ArrayList<User> getUsers();
}
