package part3;

import part3.view.ViewListener;

/**
* Main of the program:
* it is structured with an MVC architecture.
*/
public class Main {
	public static void main(String[] args) {
		try {
			ViewListener viewListener = new ViewListener();
			viewListener.display();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
