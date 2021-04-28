package part2;

import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainStatus implements Train {

    private static final String TRAIN_CODE = "numeroTreno";
    private static final String STOPS = "fermate";
    private static final String STOP_NAME = "stazione";
    private static final String LAST_DETECTION = "stazioneUltimoRilevamento";
    private static final String ORIGIN = "origine";
    private static final String DESTINATION = "destinazione";
    private static final String DEPARTURE_TIME = "orarioPartenza";
    private static final String ARRIVAL_TIME = "orarioArrivo";
    private static final String DELAY = "ritardo";
    private static final String IN_STATION = "inStazione";

    private final String trainCode;
    private final List<String> stops;
    private final String lastDetection;
    private final String origin;
    private final String destination;
    private final Date departure;
    private final Date arrival;
    private final Integer delay;
    private final Boolean inStation;

    public TrainStatus(JsonObject json) {
        this.trainCode = json.getString(TRAIN_CODE);
        this.stops = json.getJsonArray(STOPS) == null ? new ArrayList<>() : IntStream.range(0, json.getJsonArray(STOPS).size()).boxed().map(i -> json.getJsonArray(STOPS).getJsonObject(i).getString(STOP_NAME)).collect(Collectors.toList());
        this.lastDetection = json.getString(LAST_DETECTION);
        this.origin = json.getString(ORIGIN);
        this.destination = json.getString(DESTINATION);
        this.departure = new Date(json.getLong(DEPARTURE_TIME));
        this.arrival = new Date(json.getLong(ARRIVAL_TIME));
        this.delay = json.getInteger(DELAY);
        this.inStation = json.getBoolean(IN_STATION);
    }

    @Override
    public String getTrainCode() {
        return this.trainCode;
    }

    @Override
    public List<String> getStops() {
        return this.stops;
    }

    @Override
    public String getLastDetection() {
        return this.lastDetection;
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
    public String getDepartureTime() {
        return new SimpleDateFormat(TravelSolution.HOUR_FORMAT).format(this.departure);
    }

    @Override
    public String getArrivalTime() {
        return new SimpleDateFormat(TravelSolution.HOUR_FORMAT).format(this.arrival);
    }

    @Override
    public int getDelayMinutes() {
        return this.delay;
    }

    @Override
    public boolean isInStation() {
        return this.inStation;
    }

    @Override
    public String toString() {
        return "[TRAIN STATUS]: Train number "+getTrainCode()+
                " departure at "+getOrigin()+" at time "+getDepartureTime()+
                " arrival at "+getDestination()+" at time "+getArrivalTime()+
                " with current delay of "+getDelayMinutes()+" minutes.\n"+
                "[STOPS]: Stops at "+getStops().toString()+".\n"+
                "[POSITION]: Last detection at "+getLastDetection()+
                " (currently "+(isInStation() ? "" : "not ")+"in station).\n";
    }
}
