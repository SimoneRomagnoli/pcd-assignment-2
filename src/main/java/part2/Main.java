package part2;

import io.vertx.core.Vertx;
import part2.api.client.TrainAPIWebClient;

public class Main {
        public static void main(String[] args) {
            Vertx vertx = Vertx.vertx();
            TrainAPIWebClient client = new TrainAPIWebClient(vertx);

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
            /*
            Future<Train> future = client.getRealTimeTrainInfo("S01700", "8811");
            future.onSuccess(res -> {
                        System.out.println(res.toString());
                    })
                    .onFailure(err -> {
                        System.out.println("Main failed with "+err);
                    });

             */

            //TEST GET REAL TIME STATION INFO
            /*
            Future<Station> future = client.getRealTimeStationInfo("S01700", StationStatus.ArrivalsOrDepartures.ARRIVALS);
            future.onSuccess(res -> {
                        System.out.println(res.toString());
                    })
                    .onFailure(err -> {
                        System.out.println("Main failed with "+err);
                    });

            */
            System.out.println("This should be printed before future success.");

        }
    }

