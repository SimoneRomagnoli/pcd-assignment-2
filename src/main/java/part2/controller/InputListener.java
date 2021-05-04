package part2.controller;

import io.vertx.core.Future;
import part2.api.model.Station;
import part2.api.model.StationStatus;
import part2.api.model.Train;
import part2.api.model.Travel;

import java.util.List;

public interface InputListener {

    Future<List<Travel>> searchTravel(String from, String to, String date, int time);

    Future<Train> trainInfo(String trainCode, String stationCode);

    Future<String> stationCode(String trainCode);

    Future<Station> stationInfo(String stationCode, StationStatus.ArrivalsOrDepartures arrivalsOrDepartures);

    void startMonitoring(Travel travel);

    void stopMonitoring();
}
