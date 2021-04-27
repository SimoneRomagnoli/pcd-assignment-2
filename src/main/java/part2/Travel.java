package part2;

import java.util.Date;

public interface Travel {

    String getSolutionId();

    Station getOrigin();

    Station getDestination();

    String getDate();

    String getDepartureTime();

    String getArrivalTime();

    String toString();
}
