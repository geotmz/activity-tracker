package src.master;

import src.gpx.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserSingleton {
    private static final Object _instance_lock = new Object();
    private static UserSingleton _instance = null;
    private final HashMap<String, User> userData = new HashMap<>();
    private final Object userDataLock = new Object();

    private UserSingleton() {

    }

    public static UserSingleton getInstance() {
        if (_instance == null) {
            synchronized (_instance_lock) {
                _instance = new UserSingleton();
            }
        }

        return _instance;
    }

    public void addUser(String u) {
        synchronized (userDataLock) {
            userData.computeIfAbsent(u, k -> new User());
        }
    }

    public void addUser(String u, Session s) {
        synchronized (userDataLock) {
            if (userData.get(u) == null) {
                userData.put(u, new User(s));
                return;
            }

            userData.get(u).addSession(s);
        }
    }

    public void addUser(String u, ArrayList<Session> sessions) {
        synchronized (userDataLock) {
            if (userData.get(u) == null) {
                userData.put(u, new User(sessions));
                return;
            }

            userData.get(u).addSessions(sessions);
        }
    }

    public List<Session> getUserSessions(String u) {
        synchronized (userDataLock) {
            return userData.get(u).getSessions();
        }
    }

    public List<String> keyList() {
        synchronized (userDataLock) {
            return new ArrayList<>(userData.keySet());
        }
    }
}
