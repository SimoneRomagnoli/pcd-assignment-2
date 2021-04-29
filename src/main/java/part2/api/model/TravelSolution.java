package part2.api.model;

import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TravelSolution implements Travel {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String HOUR_FORMAT = "HH:mm";

    private static final String SOLUTION_ID = "idsolution";
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private static final String DEPARTURE_TIME = "departuretime";
    private static final String ARRIVAL_TIME = "arrivaltime";
    private static final String TRAIN_LIST = "trainlist";
    private static final String TRAIN_ID = "trainidentifier";

    private final String solutionId;
    private final String origin;
    private final String destination;
    private final Date departureTime;
    private final Date arrivalTime;
    private final List<String> trains;

    public TravelSolution(JsonObject json) {
        this.solutionId = json.getString(SOLUTION_ID);
        this.origin = json.getString(ORIGIN);
        this.destination = json.getString(DESTINATION);
        this.departureTime = new Date(json.getLong(DEPARTURE_TIME));
        this.arrivalTime = new Date(json.getLong(ARRIVAL_TIME));
        this.trains = IntStream
                .range(0, json.getJsonArray(TRAIN_LIST).size())
                .boxed()
                .map(i -> json.getJsonArray(TRAIN_LIST).getJsonObject(i).getString(TRAIN_ID))
                .collect(Collectors.toList());
    }

    @Override
    public String getSolutionId() {
        return this.solutionId;
    }

    @Override
    public String getOrigin() {
        return this.origin;
    }

    @Override
    public String getDestination() {
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
    public int getScales() {
        return this.trains.size()-1;
    }

    @Override
    public List<String> getTrainList() {
        return this.trains;
    }

    @Override
    public List<String> getTrainListCodes() {
        return getTrainList().stream().map(s -> s.replaceAll("[^\\d.]", "")).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "[TRAVEL SOLUTION]: from "+getOrigin()+
                " to "+getDestination()+
                " in date "+getDate()+
                " departure at "+getDepartureTime()+
                " arrival at "+getArrivalTime()+
                " with "+getScales()+" scales.\n"+
                "[TRAINS]: "+getTrainList()+"\n";
    }
}
