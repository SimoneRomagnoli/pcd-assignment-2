package part2.api.model;

import java.util.List;

public interface Station {

    List<Train> getTrains();

    int getNumberOfTrains();

    boolean isArrivalsStatus();

    boolean isDeparturesStatus();

    String toString();
}
