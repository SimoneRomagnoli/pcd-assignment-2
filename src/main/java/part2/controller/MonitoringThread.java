package part2.controller;

import io.vertx.core.Future;
import part2.api.model.Train;
import part2.api.model.Travel;
import part2.view.View;

import java.util.ArrayList;
import java.util.List;

public class MonitoringThread extends Thread {

    private static Long MINUTE = 60000L;

    private final InputListener listener;
    private final Travel travel;
    private final View view;

    private boolean monitoring;

    public MonitoringThread(InputListener listener, View view, Travel travel) {
        this.listener = listener;
        this.view = view;
        this.travel = travel;
        this.monitoring = false;
    }

    @Override
    public void run() {
        this.monitoring = true;
        while(monitoring) {
            List<Future<Train>> futures = new ArrayList<>();
            for(int i = 0; i <= this.travel.getScales(); i++) {
                final String trainCode = this.travel.getTrainListCodes().get(i);
                final Future<String> futureStationCode = this.listener.stationCode(trainCode);
                futureStationCode.onSuccess(stationCode -> {
                    final int startIndex = stationCode.indexOf("|")+stationCode.indexOf("-")+1;
                    final String parsedCode = stationCode.substring(startIndex, startIndex+6);
                    //System.out.println("Parsed code is "+parsedCode);
                    futures.add(this.listener.trainInfo(trainCode, parsedCode));
                });
            }
            this.view.update(futures);

            try {
                Thread.sleep(MINUTE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void end() {
        this.monitoring = false;
    }
}
