package part2;

import io.vertx.core.Vertx;
import part2.controller.Controller;
import part2.view.View;

public class Main {
        public static void main(String[] args) {
            Vertx vertx = Vertx.vertx();
            View view = new View();
            Controller controller = new Controller(view, vertx);
            view.addListener(controller);
            view.display();
        }
    }

