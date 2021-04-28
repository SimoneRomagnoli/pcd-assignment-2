package part2;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainAPIWebClient {

    private static final int HTTPS_PORT = 443;
    private static final String HOST_GET_TRAIN_SOLUTIONS = "www.lefrecce.it";
    private static final String REQUEST_URI_GET_TRAIN_SOLUTIONS = "/msite/api/solutions?arflag=A&adultno=1&childno=0&direction=A&frecce=false&onlyRegional=false";

    private static final int HTTP_PORT = 80;
    private static final String HOST_GET_REAL_TIME_INFO = "www.viaggiatreno.it";
    private static final String REQUEST_URI_GET_REAL_TIME_TRAIN_INFO = "/viaggiatrenomobile/resteasy/viaggiatreno/andamentoTreno/";
    private static final String REQUEST_URI_GET_REAL_TIME_STATION_INFO = "/viaggiatrenonew/resteasy/viaggiatreno/";

    private final WebClient client;

    private final SimpleDateFormat dateFormatter;

    public TrainAPIWebClient(final Vertx vertx) {
        this.client = WebClient.create(vertx);
        this.dateFormatter = new SimpleDateFormat(TravelSolution.DATE_FORMAT);
    }

    public Future<List<Travel>> getTrainSolutions(String from, String to, String startingDate, int startingTime) {
        try {
            Date date = this.dateFormatter.parse(startingDate);
            Promise<JsonArray> jsonResponse = getRequestForTravelSolutions(from, to, date, startingTime);
            return futureTravelSolutions(jsonResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Future<Train> getRealTimeTrainInfo(String stationName, String trainCode) {
        try {
            Promise<JsonObject> jsonResponse = getRequestForRealTimeTrainInfo(stationName, trainCode, System.currentTimeMillis());
            return futureRealTimeTrainInfo(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Future<Station> getRealTimeStationInfo(String stationName, StationStatus.ARRIVALS_OR_DEPARTURES arrivalsOrDepartures) {
        try {
            Promise<JsonArray> jsonResponse = getRequestForRealTimeStationInfo(stationName, arrivalsOrDepartures, Calendar.getInstance().getTime());
            return futureRealTimeStationInfo(jsonResponse, arrivalsOrDepartures);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Promise<JsonArray> getRequestForTravelSolutions(String origin, String destination, Date date, int time) {
        Promise<JsonArray> result = Promise.promise();
        try {
            this.client
                    .get(HTTPS_PORT, HOST_GET_TRAIN_SOLUTIONS, REQUEST_URI_GET_TRAIN_SOLUTIONS)
                    .ssl(true)
                    .addQueryParam("origin", origin)
                    .addQueryParam("destination", destination)
                    .addQueryParam("adate", this.dateFormatter.format(date))
                    .addQueryParam("atime", String.valueOf(time))
                    .send()
                    .onSuccess(response -> {
                        //System.out.println("Received response with status code " + response.statusCode());
                        result.complete(response.bodyAsJsonArray());
                    })
                    .onFailure(err -> {
                        System.out.println("Something went wrong in GET request: " + err.getMessage());
                        result.fail(err.getMessage());
                    });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.fail("Something went wrong in GET request: exception encountered");
        return result;
    }

    private Future<List<Travel>> futureTravelSolutions(Promise<JsonArray> jsonPromise) {
        Promise<List<Travel>> travels = Promise.promise();
        jsonPromise
                .future()
                .onFailure(err -> {
                    System.out.println("Something went wrong in JSON response: " + err.getMessage());
                    travels.fail(err.getMessage());
                })
                .onSuccess(jsonArray -> {
                    //System.out.println("Received a json array: " + jsonArray);
                    /* HERE IMPLEMENT SOMETHING: I STILL DON'T KNOW WHAT */
                })
                .onComplete(asyncResult -> {
                    //System.out.println("Promise completed with: " + asyncResult.result());
                    travels.complete(
                            IntStream
                                    .range(0, asyncResult.result().size())
                                    .boxed()
                                    .map(i -> new TravelSolution(asyncResult.result().getJsonObject(i)))
                                    .collect(Collectors.toList())
                    );
                });

        return travels.future();
    }

    private Promise<JsonObject> getRequestForRealTimeTrainInfo(String stationName, String trainCode, Long millisDate) {
        Promise<JsonObject> result = Promise.promise();
        try {
            this.client
                    .get(HTTP_PORT, HOST_GET_REAL_TIME_INFO, REQUEST_URI_GET_REAL_TIME_TRAIN_INFO
                                    +stationName+"/"
                                    +trainCode+"/"
                                    +millisDate)
                    .send()
                    .onSuccess(response -> {
                        System.out.println("Received response with status code " + response.statusCode());
                        result.complete(response.bodyAsJsonObject());
                    })
                    .onFailure(err -> {
                        System.out.println("Something went wrong in GET request: " + err.getMessage());
                        result.fail(err.getMessage());
                    });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.fail("Something went wrong in GET request: exception encountered");
        return result;
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

    private Promise<JsonArray> getRequestForRealTimeStationInfo(String stationName, StationStatus.ARRIVALS_OR_DEPARTURES arrivalsOrDepartures, Date time) {
        Promise<JsonArray> result = Promise.promise();
        try {
            this.client
                    .get(HTTP_PORT, HOST_GET_REAL_TIME_INFO, REQUEST_URI_GET_REAL_TIME_STATION_INFO
                            +(arrivalsOrDepartures.equals(StationStatus.ARRIVALS_OR_DEPARTURES.DEPARTURES) ? "partenze" : "arrivi")+"/"
                            +stationName+"/"
                            +time.toString())
                    .send()
                    .onSuccess(response -> {
                        System.out.println("Received response with status code " + response.statusCode());
                        result.complete(response.bodyAsJsonArray());
                    })
                    .onFailure(err -> {
                        System.out.println("Something went wrong in GET request: " + err.getMessage());
                        result.fail(err.getMessage());
                    });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.fail("Something went wrong in GET request: exception encountered");
        return result;
    }

    private Future<Station> futureRealTimeStationInfo(Promise<JsonArray> jsonPromise, StationStatus.ARRIVALS_OR_DEPARTURES arrivalsOrDepartures) {
        Promise<Station> stationStatus = Promise.promise();
        jsonPromise
                .future()
                .onFailure(err -> {
                    System.out.println("Something went wrong in JSON response: " + err.getMessage());
                    stationStatus.fail(err.getMessage());
                })
                .onSuccess(json -> {
                    //System.out.println("Received a json array: " + json);
                    /* HERE IMPLEMENT SOMETHING: I STILL DON'T KNOW WHAT */
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
