package part2.api.model;

import java.util.List;

public interface Travel {

    String getSolutionId();

    String getOrigin();

    String getDestination();

    String getDate();

    String getDepartureTime();

    String getArrivalTime();

    int getScales();

    List<String> getTrainList();

    List<String> getTrainListCodes();
}
