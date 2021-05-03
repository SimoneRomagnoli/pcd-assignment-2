package part3.v2;

import part3.v2.view.View;

/**
* Main of the program:
* it is structured with an MVC architecture.
*/
public class Main {
	public static void main(String[] args) {
		try {
			View view = new View();
			view.display();						
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
