package part2;

import io.vertx.core.json.JsonObject;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class TravelSolution implements Travel {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String HOUR_FORMAT = "HH:mm";

    private static final String SOLUTION_ID = "idsolution";
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private static final String DEPARTURE_TIME = "departuretime";
    private static final String ARRIVAL_TIME = "arrivaltime";

    private final String solutionId;
    private final Station origin;
    private final Station destination;
    private final Date departureTime;
    private final Date arrivalTime;

    public TravelSolution(String solutionId, Station origin, Station destination, Date departureTime, Date arrivalTime) {
        this.solutionId = solutionId;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;

    }

    public TravelSolution(JsonObject json) {
        this.solutionId = json.getString(SOLUTION_ID);
        this.origin = new StationImpl(json.getString(ORIGIN));
        this.destination = new StationImpl(json.getString(DESTINATION));
        this.departureTime = new Date(json.getLong(DEPARTURE_TIME));
        this.arrivalTime = new Date(json.getLong(ARRIVAL_TIME));

    }

    @Override
    public String getSolutionId() {
        return this.solutionId;
    }

    @Override
    public Station getOrigin() {
        return this.origin;
    }

    @Override
    public Station getDestination() {
        return this.destination;
    }

    @Override
    public String getDate() {
        return new SimpleDateFormat(DATE_FORMAT).format(this.departureTime);
    }

    @Override
    public String getDepartureTime() {
        return new SimpleDateFormat(HOUR_FORMAT).format(this.departureTime);
    }

    @Override
    public String getArrivalTime() {
        return new SimpleDateFormat(HOUR_FORMAT).format(this.arrivalTime);
    }

    @Override
    public String toString() {
        return "[TRAVEL SOLUTION]: from "+this.origin.getName()+
                " to "+this.destination.getName()+
                " in date "+getDate()+
                " departure at "+getDepartureTime()+
                " arrival at "+getArrivalTime();
    }
}
