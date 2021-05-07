package part2.controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import part2.api.model.Train;
import part2.api.model.Travel;
import part2.view.View;

public class MonitoringVerticle extends AbstractVerticle {

    private static Long MINUTE = 60000L;

    private final InputListener listener;
    private final Travel travel;
    private final View view;

    private Long id;

    public MonitoringVerticle(InputListener listener, View view, Travel travel) {
        this.listener = listener;
        this.view = view;
        this.travel = travel;
    }

    @Override
    public void start() {
        getRealTimeInfo();

        this.id = this.getVertx().setPeriodic(MINUTE, (t) -> {
            getRealTimeInfo();
        });
    }

    @Override
    public void stop() {
        try {
            this.getVertx().cancelTimer(this.id);
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRealTimeInfo() {
        for(int i = 0; i <= this.travel.getScales(); i++) {
            final String trainCode = this.travel.getTrainListCodes().get(i);
            final Future<String> futureStationCode = this.listener.stationCode(trainCode);
            final int index = i;

            futureStationCode.onSuccess(stationCode -> {
                final int startIndex = stationCode.indexOf("|")+stationCode.indexOf("-")+1;
                final String parsedCode = stationCode.substring(startIndex, startIndex+6);
                Future<Train> train = this.listener.trainInfo(trainCode, parsedCode);
                this.view.update(train, index);
            });
        }
    }
}
