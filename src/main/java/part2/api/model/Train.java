package part2.api.model;

import java.util.List;

public interface Train {

    String getTrainCode();

    List<String> getStops();

    String getLastDetection();

    String getOrigin();

    String getDestination();

    String getDepartureTime();

    String getArrivalTime();

    int getDelayMinutes();

    boolean isInStation();

    String toString();

}
