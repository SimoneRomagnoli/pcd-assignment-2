package part2.api.client;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import part2.api.model.StationStatus;
import part2.api.model.TravelSolution;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Requests {

    private static final int HTTPS_PORT = 443;
    private static final String HOST_GET_TRAIN_SOLUTIONS = "www.lefrecce.it";
    private static final String REQUEST_URI_GET_TRAIN_SOLUTIONS = "/msite/api/solutions?arflag=A&adultno=1&childno=0&direction=A&frecce=false&onlyRegional=false";

    private static final int HTTP_PORT = 80;
    private static final String HOST_GET_REAL_TIME_INFO = "www.viaggiatreno.it";
    private static final String REQUEST_URI_GET_REAL_TIME_TRAIN_INFO = "/viaggiatrenomobile/resteasy/viaggiatreno/andamentoTreno/";
    private static final String REQUEST_URI_GET_REAL_TIME_STATION_INFO = "/viaggiatrenonew/resteasy/viaggiatreno/";

    private final WebClient client;
    private final SimpleDateFormat dateFormatter;

    public Requests(final WebClient client) {
        this.client = client;
        this.dateFormatter = new SimpleDateFormat(TravelSolution.DATE_FORMAT);
    }

    public Promise<JsonArray> getRequestForTravelSolutions(String origin, String destination, String date, int time) {
        Promise<JsonArray> result = Promise.promise();
        try {
            this.client
                    .get(HTTPS_PORT, HOST_GET_TRAIN_SOLUTIONS, REQUEST_URI_GET_TRAIN_SOLUTIONS)
                    .ssl(true)
                    .addQueryParam("origin", origin)
                    .addQueryParam("destination", destination)
                    .addQueryParam("adate", this.dateFormatter.format(this.dateFormatter.parse(date)))
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

    public Promise<JsonObject> getRequestForRealTimeTrainInfo(String stationName, String trainCode, Long millisDate) {
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

    public Promise<JsonArray> getRequestForRealTimeStationInfo(String stationName, StationStatus.ArrivalsOrDepartures arrivalsOrDepartures, Date time) {
        Promise<JsonArray> result = Promise.promise();
        try {
            this.client
                    .get(HTTP_PORT, HOST_GET_REAL_TIME_INFO, REQUEST_URI_GET_REAL_TIME_STATION_INFO
                            +(arrivalsOrDepartures.equals(StationStatus.ArrivalsOrDepartures.DEPARTURES) ? "partenze" : "arrivi")+"/"
                            +stationName+"/"
                            +time.toString().replace("CEST", "UTC").replace(" ", "%20"))
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

}
