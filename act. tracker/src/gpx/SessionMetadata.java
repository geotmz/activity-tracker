package src.gpx;

import java.io.Serializable;

/**
 * @param time unix time
 */
public record SessionMetadata(
        double distance, // in meters
        double speed, // km/h
        double elevationGain,
        double elevationLoss,
        long time // unix time
) implements Serializable {

}
