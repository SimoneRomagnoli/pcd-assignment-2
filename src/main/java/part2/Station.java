package part2;

import java.util.List;

public interface Station {

    List<Train> getTrains();

    int getNumberOfTrains();

    boolean isArrivalsStatus();

    boolean isDeparturesStatus();

    String toString();
}
