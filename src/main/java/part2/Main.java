package part2;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.List;

public class Main {
        public static void main(String[] args) {
            Vertx vertx = Vertx.vertx();
            TrainWebClient client = new TrainWebClient(vertx);
            Future<List<Travel>> future = client.getTrainSolutions("MILANO CENTRALE", "ROMA TERMINI", "28/05/2019", 17);
            future
                    .onComplete(res -> {
                        //System.out.println("Main completed with "+res);
                    })
                    .onSuccess(travels -> {
                        System.out.println("Le possibili soluzioni sono:");
                        for(Travel travel:travels) {
                            System.out.println(travel.toString());
                        }
                        //System.out.println("Main succeeded with "+res);
                    })
                    .onFailure(err -> {
                        //System.out.println("Main failed with "+err);
                    });

        }
    }

