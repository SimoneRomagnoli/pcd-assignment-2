package part2.controller;

import io.vertx.core.Vertx;
import part2.api.client.TrainAPIWebClient;
import part2.view.View;

public class Controller implements InputListener {

    private final View view;
    private final Vertx vertx;
    private final TrainAPIWebClient client;

    public Controller(View view, Vertx vertx) {
        this.view = view;
        this.vertx = vertx;
        this.client = new TrainAPIWebClient(this.vertx);
    }

    @Override
    public void searchTravel(String from, String to, String date, int time) {
        this.client.getTrainSolutions(from, to, date, time);
    }
}
