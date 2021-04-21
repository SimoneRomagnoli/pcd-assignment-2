import controller.Controller;
import view.View;

/**
* Main of the program:
* it is structured with an MVC architecture.
*/
public class Main {
	public static void main(String[] args) {
		try {
			View view = new View();
			Controller controller = new Controller(view);

			view.addListener(controller);
			view.display();						
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
