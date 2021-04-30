package part2.controller;

import part2.api.model.StationStatus;

public interface InputListener {

    void searchTravel(String from, String to, String date, int time);

    void trainInfo(String trainCode, String stationCode);

    void stationInfo(String stationCode, StationStatus.ArrivalsOrDepartures arrivalsOrDepartures);
}
