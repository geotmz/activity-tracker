package src.utils;

import src.gpx.Session;
import src.gpx.SessionMetadata;
import src.gpx.Waypoint;

import java.util.ArrayList;

public class Utilities {
    public final static int DISCOVERY_PORT = 8000;
    public final static String DISCOVERY_IP = "0.0.0.0";

    public final static int LOCAL_DISCOVERY_PORT = 12500;
    public final static String LOCAL_DISCOVERY_IP = "localhost";

    private Utilities() {
    }

    public static SessionMetadata calculateMetrics(Session session) {
        ArrayList<Waypoint> segment = session.getSegment();
        double totalDistance = .0;
        double totalElevationGain = .0;
        double totalElevationLoss = .0;
        // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
        for (int i = 1; i < segment.size(); i++) {

            double latPrev = segment.get(i - 1).getLatitude();
            double lonPrev = segment.get(i - 1).getLongitude();
            double elePrev = segment.get(i - 1).getElevation();

            double latCurr = segment.get(i).getLatitude();
            double lonCurr = segment.get(i).getLongitude();
            double eleCurr = segment.get(i).getElevation();

            final int R = 6371; // Radius of the earth

            double latDistance = Math.toRadians(latCurr - latPrev);
            double lonDistance = Math.toRadians(lonCurr - lonPrev);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(latPrev)) * Math.cos(Math.toRadians(latCurr))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c * 1000; // convert to meters

            double height = elePrev - eleCurr;

            if (height > 0) totalElevationGain += height;
            if (height < 0) totalElevationLoss += Math.abs(height);

            distance = Math.pow(distance, 2) + Math.pow(height, 2);

            totalDistance += Math.sqrt(distance); // in meters
        }

        long totalTime = segment.get(segment.size() - 1).getTime() - segment.get(0).getTime();

        // total distance is in meters; (unix) time is the difference between end time and start time in seconds
        //  difference between m/s (meters per second) and km/h is a factor of 3.6
        double totalSpeed = (totalDistance / totalTime) * 3.6;

        return new SessionMetadata(totalDistance, totalSpeed, totalElevationGain, totalElevationLoss, totalTime);
    }

    public static SessionMetadata averageSubsessionsMetadata(SessionMetadata[] submetadata) {
        double distance = 0;
        double elevationGain = 0;
        double elevationLoss = 0;
        long time = 0;

        for (SessionMetadata submetadatum : submetadata) {
            distance += submetadatum.distance();
            elevationGain += submetadatum.elevationGain();
            elevationLoss += submetadatum.elevationLoss();
            time += submetadatum.time();
        }

        double speed = (distance / time) * 3.6;

        return new SessionMetadata(distance, speed, elevationGain, elevationLoss, time);
    }

}
