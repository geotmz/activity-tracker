package src.gpx;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Waypoint implements Serializable {
    private final double lat;
    private final double lon;
    private final double ele;
    private final long time; //unix time

    public Waypoint(double lat, double lon, double ele, String datetime) throws ParseException {
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        // https://stackoverflow.com/questions/7784421/getting-unix-timestamp-from-date
        //  .getTime() returns unix time * 1000, so we're doing the reverse to retrieve unix time
        // also, SimpleDateFormat class and .parse() method are NOT thread safe
        this.time = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).parse(datetime).getTime() / 1000;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lon;
    }

    public double getElevation() {
        return ele;
    }

    public long getTime() {
        return time;
    }
}
