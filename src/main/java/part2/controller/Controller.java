package part2.controller;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import part2.api.client.TrainAPIWebClient;
import part2.api.model.Station;
import part2.api.model.StationStatus;
import part2.api.model.Train;
import part2.api.model.Travel;
import part2.view.View;

import java.util.List;

public class Controller implements InputListener {

    private final View view;
    private final Vertx vertx;
    private final TrainAPIWebClient client;

    private MonitoringVerticle monitoringVerticle;

    public Controller(View view, Vertx vertx) {
        this.view = view;
        this.vertx = vertx;
        this.client = new TrainAPIWebClient(this.vertx);
    }

    @Override
    public Future<List<Travel>> searchTravel(String from, String to, String date, int time) {
        Future<List<Travel>> travels = this.client.getTrainSolutions(from, to, date, time);
        //travels.onSuccess(res -> res.forEach(t -> System.out.println(t.toString())) );
        System.out.println("Search travel request submitted");
        return travels;
    }

    @Override
    public Future<Train> trainInfo(String trainCode) {
        Promise<Train> promiseTrain = Promise.promise();
        Future<String> futureStationCode = stationCode(trainCode);
        futureStationCode.onSuccess(stationCode -> {
            final int startIndex = stationCode.indexOf("|")+stationCode.indexOf("-")+1;
            final String parsedCode = stationCode.substring(startIndex, startIndex+6);
            Future<Train> futureTrain = this.client.getRealTimeTrainInfo(parsedCode, trainCode);
            futureTrain.onSuccess(promiseTrain::complete);
        });
        //train.onSuccess(res -> System.out.println(res.toString()));
        System.out.println("Real time train info request submitted");
        return promiseTrain.future();
    }

    @Override
    public Future<Train> trainInfo(String trainCode, String stationCode) {
        Future<Train> train = this.client.getRealTimeTrainInfo(stationCode, trainCode);
        //train.onSuccess(res -> System.out.println(res.toString()));
        System.out.println("Real time train info request submitted");
        return train;
    }

    @Override
    public Future<String> stationCode(String trainCode) {
        Future<String> stationCode = this.client.getDepartureStationCode(trainCode);
        //stationCode.onSuccess(System.out::println);
        System.out.println("Departure station code request submitted");
        return stationCode;
    }

    @Override
    public Future<Station> stationInfo(String stationCode, StationStatus.ArrivalsOrDepartures arrivalsOrDepartures) {
        Future<Station> station = this.client.getRealTimeStationInfo(stationCode, arrivalsOrDepartures);
        station.onSuccess(res -> System.out.println(res.toString()));
        System.out.println("Real time station info request submitted");
        return station;
    }

    @Override
    public void startMonitoring(Travel travel) {
        this.monitoringVerticle = new MonitoringVerticle(this, this.view, travel);
        this.vertx.deployVerticle(this.monitoringVerticle);
    }

    @Override
    public void stopMonitoring() {
        this.monitoringVerticle.stop();
    }
}
