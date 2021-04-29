package part2.controller;

public interface InputListener {

    void searchTravel(String from, String to, String date, int time);

    void trainInfo(String trainCode, String stationCode);
}
