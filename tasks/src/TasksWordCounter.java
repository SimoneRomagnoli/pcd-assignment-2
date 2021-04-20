import controller.Controller;
import gui.View;
import model.Model;

import java.io.IOException;

/**
 * Main of the program:
 * it is structured with an MVC architecture.
 */
public class TasksWordCounter {

    public static void main(String[] args) {
        final Model model = new Model();
        final Controller controller = new Controller(model);
        final View view = new View(controller);
        model.addObserver(view);
    }

}
