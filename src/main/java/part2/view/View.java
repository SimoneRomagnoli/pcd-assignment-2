package part2.view;

import io.vertx.core.Future;
import part2.api.model.Train;
import part2.controller.InputListener;

import java.util.List;

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
		frame.setInputListener(l);
	}

	public void display() {
        javax.swing.SwingUtilities.invokeLater(() -> {
        	frame.setVisible(true);
        });
    }

    public void update(Future<Train> monitoredTrain, int index) {
		this.frame.updateMonitoring(monitoredTrain, index);
	}

}
	
