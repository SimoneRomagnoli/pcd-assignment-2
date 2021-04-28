package part2;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class Main {
        public static void main(String[] args) {
            Vertx vertx = Vertx.vertx();
            TrainWebClient client = new TrainWebClient(vertx);

            //TEST GET TRAIN SOLUTIONS
            /*
            Future<List<Travel>> future = client.getTrainSolutions("MILANO CENTRALE", "ROMA TERMINI", "28/05/2019", 17);
            future.onSuccess(travels -> {
                        System.out.println("Le possibili soluzioni sono:");
                        for(Travel travel:travels) {
                            System.out.println(travel.toString());
                        }
                        //System.out.println("Main succeeded with "+res);
                    })
                    .onFailure(err -> {
                        //System.out.println("Main failed with "+err);
                    });

             */

            //TEST GET REAL TIME TRAIN INFO
            ///*
            Future<Train> future = client.getRealTimeTrainInfo("S01700", "8811", System.currentTimeMillis());
            future.onSuccess(res -> {
                        System.out.println(res.toString());
                    })
                    .onFailure(err -> {
                        System.out.println("Main failed with "+err);
                    });

             //*/
            System.out.println("This should be printed before future success.");

        }
    }

