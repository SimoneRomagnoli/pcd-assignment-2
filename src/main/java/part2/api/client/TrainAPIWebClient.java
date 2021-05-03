package part2.api.client;

import io.vertx.core.*;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import part2.api.model.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainAPIWebClient {

    private final WebClient client;
    private final Requests requests;

    public TrainAPIWebClient(final Vertx vertx) {
        this.client = WebClient.create(vertx);
        this.requests = new Requests(this.client);
    }

    /**
     *
     * Method that returns up to 5 possible travel solutions,
     * starting from an origin and a destination stations, a date and an hour of the day (0-24).
     *
     * @param from
     * @param to
     * @param startingDate
     * @param startingTime
     * @return a list of travels
     */
    public Future<List<Travel>> getTrainSolutions(String from, String to, String startingDate, int startingTime) {
        try {
            Promise<JsonArray> jsonResponse = this.requests.getRequestForTravelSolutions(from, to, startingDate, startingTime);
            return futureTravelSolutions(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Future<List<Travel>> futureTravelSolutions(Promise<JsonArray> jsonPromise) {
        Promise<List<Travel>> travels = Promise.promise();
        jsonPromise
                .future()
                .onFailure(err -> {
                    System.out.println("Something went wrong in JSON response: " + err.getMessage());
                    travels.fail(err.getMessage());
                })
                .onComplete(asyncResult -> {
                    //System.out.println("Promise completed with: " + asyncResult.result());
                     List<Travel> completeTravels = IntStream
                                    .range(0, asyncResult.result().size())
                                    .boxed()
                                    .map(i -> new TravelSolution(asyncResult.result().getJsonObject(i)))
                                    .collect(Collectors.toList());

                    for(Travel travel:completeTravels) {
                        Promise<JsonArray> jsonDetails = this.requests.getRequestForSolutionDetail(travel.getSolutionId());
                        jsonDetails.future()
                                .onFailure(err -> {
                                    System.out.println("Something went wrong in JSON response: " + err.getMessage());
                                    //jsonDetails.fail(err.getMessage());
                                })
                                .onSuccess(travel::addDetails);
                    }

                    travels.complete(completeTravels);
                });

        return travels.future();
    }

    /**
     *
     * Method that retrieves real time information about a single train.
     *
     * @param stationName
     * @param trainCode
     * @return
     */
    public Future<Train> getRealTimeTrainInfo(String stationName, String trainCode) {
        try {
            Promise<JsonObject> jsonResponse = this.requests.getRequestForRealTimeTrainInfo(stationName, trainCode, System.currentTimeMillis());
            return futureRealTimeTrainInfo(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Future<Train> futureRealTimeTrainInfo(Promise<JsonObject> jsonPromise) {
        Promise<Train> trainStatus = Promise.promise();
        jsonPromise
                .future()
                .onFailure(err -> {
                    System.out.println("Something went wrong in JSON response: " + err.getMessage());
                    trainStatus.fail(err.getMessage());
                })
                .onSuccess(json -> {
                    //System.out.println("Received a json array: " + json);
                    /* HERE IMPLEMENT SOMETHING: I STILL DON'T KNOW WHAT */
                })
                .onComplete(asyncResult -> {
                    //System.out.println("Promise completed with: " + asyncResult.result());
                    trainStatus.complete(
                            new TrainStatus(asyncResult.result())
                    );
                });

        return trainStatus.future();
    }

    /**
     *
     * Method that retrieves real time information about a single station.
     *
     * @param stationName
     * @param arrivalsOrDepartures
     * @return
     */
    public Future<Station> getRealTimeStationInfo(String stationName, StationStatus.ArrivalsOrDepartures arrivalsOrDepartures) {
        try {
            Promise<JsonArray> jsonResponse = this.requests.getRequestForRealTimeStationInfo(stationName, arrivalsOrDepartures, Calendar.getInstance().getTime());
            return futureRealTimeStationInfo(jsonResponse, arrivalsOrDepartures);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Future<Station> futureRealTimeStationInfo(Promise<JsonArray> jsonPromise, StationStatus.ArrivalsOrDepartures arrivalsOrDepartures) {
        Promise<Station> stationStatus = Promise.promise();
        jsonPromise
                .future()
                .onFailure(err -> {
                    System.out.println("Something went wrong in JSON response: " + err.getMessage());
                    stationStatus.fail(err.getMessage());
                })
                .onComplete(asyncResult -> {
                    //System.out.println("Promise completed with: " + asyncResult.result());
                    stationStatus.complete(
                            new StationStatus(asyncResult.result(), arrivalsOrDepartures)
                    );
                });

        return stationStatus.future();
    }

}
