package part2;

import java.sql.Date;

public interface Travel {

    String getSolutionId();

    Station getOrigin();

    Station getDestination();

    Date getDepartureDate();

    Long getDepartureTime();

    Long getArrivalTime();

    String toString();
}
