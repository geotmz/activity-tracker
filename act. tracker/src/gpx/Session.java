package src.gpx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Session implements Serializable {
    private final String user;
    private final ArrayList<Waypoint> segment = new ArrayList<>();
    private SessionMetadata metadata;
    private transient Object metadataLock = new Object();
    private transient Object segmentLock = new Object();

    public Session(String user) {
        this.user = user;
    }

    public Session(String user, List<Waypoint> segment) {
        this.user = user;

        for (var w : segment) {
            addWaypoint(w);
        }
    }

    public void addWaypoint(Waypoint w) {
        if (segmentLock == null) {
            synchronized (this) {
                segmentLock = new Object();
            }
        }
        synchronized (segmentLock) {
            segment.add(w);
        }
    }

    public ArrayList<Waypoint> getSegment() {
        if (segmentLock == null) {
            synchronized (this) {
                segmentLock = new Object();
            }
        }
        synchronized (segmentLock) {
            // shallow copy; waypoint class is immutable, and by extension the entire contents of the list are so as well
            return new ArrayList<>(segment);
        }
    }

    public Session[] splitSessionsEqually(int segments) {
        if (segmentLock == null) {
            synchronized (this) {
                segmentLock = new Object();
            }
        }
        synchronized (segmentLock) {
            int i = 0;
            Session[] subsessions = new Session[segments];

            // https://codereview.stackexchange.com/a/27930
            int size = (int) Math.ceil((float) segment.size() / segments);
            for (int start = 0; start < segment.size(); start += size) {
                int end = Math.min(start + size, segment.size());
                subsessions[i++] = new Session(user, segment.subList(start, end));
            }

            return subsessions;
        }
    }

    public String getUser() {
        return user;
    }

    public SessionMetadata getMetadata() {
        if (metadataLock == null) {
            synchronized (this) {
                metadataLock = new Object();
            }
        }
        synchronized (metadataLock) {
            return metadata;
        }
    }

    public void setMetadata(SessionMetadata metadata) {
        if (metadataLock == null) {
            synchronized (this) {
                metadataLock = new Object();
            }
        }
        synchronized (metadataLock) {
            this.metadata = metadata;
        }
    }

    @Override
    public String toString() {
        return "Session{" +
                "user='" + user + '\'' +
                ", metadata=" + metadata +
                ", metadataLock=" + metadataLock +
                ", segment=" + segment +
                ", segmentLock=" + segmentLock +
                '}';
    }
}
