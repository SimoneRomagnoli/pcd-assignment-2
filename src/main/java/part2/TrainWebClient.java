package part2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainWebClient extends AbstractVerticle {

    private static final int PORT = 443;
    private static final String HOST = "www.lefrecce.it";
    private static final String REQUEST_URI_WITH_STANDARD_PARAMS = "/msite/api/solutions?arflag=A&adultno=1&childno=0&direction=A&frecce=false&onlyRegional=false";

    private final WebClient client;

    private SimpleDateFormat dateFormatter;

    public TrainWebClient(final Vertx vertx) {
        this.client = WebClient.create(vertx);
        this.dateFormatter = new SimpleDateFormat(TravelSolution.DATE_FORMAT);
    }

    public Future<List<Travel>> getTrainSolutions(String from, String to, String startingDate, int startingTime) {
        try {
            Station origin = new StationImpl(from);
            Station destination = new StationImpl(to);
            Date date = this.dateFormatter.parse(startingDate);
            Promise<JsonArray> jsonResponse = getRequest(origin, destination, date, startingTime);

            Promise<List<Travel>> travels = Promise.promise();
            jsonResponse
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Promise<JsonArray> getRequest(Station origin, Station destination, Date date, int time) {
        Promise<JsonArray> result = Promise.promise();
        try {
            this.client
                    .get(PORT, HOST, REQUEST_URI_WITH_STANDARD_PARAMS)
                    .ssl(true)
                    .addQueryParam("origin", origin.getName())
                    .addQueryParam("destination", destination.getName())
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
}
