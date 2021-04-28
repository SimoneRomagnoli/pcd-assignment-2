package part2;

import io.vertx.core.json.JsonArray;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StationStatus implements Station {

    public enum ARRIVALS_OR_DEPARTURES {
        ARRIVALS, DEPARTURES
    }

    private final ARRIVALS_OR_DEPARTURES arrivalsOrDepartures;
    private final List<Train> trains;

    public StationStatus(JsonArray json, ARRIVALS_OR_DEPARTURES arrivalsOrDepartures) {
        this.arrivalsOrDepartures = arrivalsOrDepartures;
        this.trains = IntStream.range(0, json.size()).boxed().map(i -> new TrainStatus(json.getJsonObject(i))).collect(Collectors.toList());
    }

    @Override
    public List<Train> getTrains() {
        return this.trains;
    }

    @Override
    public int getNumberOfTrains() {
        return this.trains.size();
    }

    @Override
    public boolean isArrivalsStatus() {
        return this.arrivalsOrDepartures.equals(ARRIVALS_OR_DEPARTURES.ARRIVALS);
    }

    @Override
    public boolean isDeparturesStatus() {
        return this.arrivalsOrDepartures.equals(ARRIVALS_OR_DEPARTURES.DEPARTURES);
    }

    @Override
    public String toString() {
        return (isArrivalsStatus() ? "[ARRIVI]: " : "[PARTENZE] ")+getNumberOfTrains();
    }

}
