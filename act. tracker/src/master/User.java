package src.master;

import src.gpx.Session;

import java.util.ArrayList;
import java.util.LinkedList;

public class User {

    private final LinkedList<Session> sessions = new LinkedList<>();

    public User() {

    }

    public User(Session s) {
        addSession(s);
    }

    public User(ArrayList<Session> s) {
        addSessions(s);
    }

    public LinkedList<Session> getSessions() {
        return new LinkedList<>(sessions);
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void addSessions(ArrayList<Session> sessions) {
        this.sessions.addAll(sessions);
    }
}
