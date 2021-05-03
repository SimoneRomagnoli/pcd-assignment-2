package part2.api.model;

import io.vertx.core.json.JsonObject;

public class TravelDetails {

    private static final String IDENTIFIER = "trainidentifier";
    private static final String DEPARTURE_STATION = "departurestation";
    private static final String ARRIVAL_STATION = "arrivalstation";
    private static final String DEPARTURE_TIME = "departuretime";
    private static final String ARRIVAL_TIME = "arrivaltime";
    private static final String DURATION = "duration";

    private final String identifier;
    private final String departureStation;
    private final String arrivalStation;
    private final String departureTime;
    private final String arrivalTime;
    private final String duration;

    public TravelDetails(JsonObject details) {
        this.identifier = details.getString(IDENTIFIER);
        this.departureStation = details.getString(DEPARTURE_STATION);
        this.arrivalStation = details.getString(ARRIVAL_STATION);
        this.departureTime = details.getString(DEPARTURE_TIME);
        this.arrivalTime = details.getString(ARRIVAL_TIME);
        this.duration = details.getString(DURATION);
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getCode() {
        return this.identifier.replaceAll("[^\\d.]", "");
    }

    public String getDepartureStation() {
        return this.departureStation;
    }

    public String getDepartureTime() {
        return this.departureTime;
    }

    public String getArrivalStation() {
        return this.arrivalStation;
    }

    public String getArrivalTime() {
        return this.arrivalTime;
    }

    public String getDuration() {
        return  this.duration;
    }
}
