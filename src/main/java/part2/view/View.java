package part2.view;

import part2.controller.InputListener;

/**
 * Class representing the view part of the application.
 *
 */
public class View {

	private ViewFrame frame;

	public View(){
		frame = new ViewFrame();
	}
	
	public void addListener(InputListener l){
		frame.addListener(l);
	}

	public void display() {
        javax.swing.SwingUtilities.invokeLater(() -> {
        	frame.setVisible(true);
        });
    }
	
	public void update() {
		frame.update();
	}
	
	public void done() {
		frame.done();
	}

}
	
