package part2.api.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Optional;

public interface Travel {

    String getSolutionId();

    String getOrigin();

    String getDestination();

    String getDepartureDate();

    String getArrivalDate();

    String getDepartureTime();

    String getArrivalTime();

    int getScales();

    List<String> getTrainList();

    List<String> getTrainListCodes();

    void addDetails(JsonArray details);

    Optional<List<TravelDetails>> getDetails();
}
