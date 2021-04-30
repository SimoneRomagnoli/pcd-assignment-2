package part3.view;

import part3.controller.Flag;

public class Viewer extends Thread {

	private View view;
	private Flag done;
	
	public Viewer(View view, Flag done) {
		super("viewer");
		this.view = view;
		this.done = done;
	}

	public void run() {
		while (!done.isSet()) {
			try {
				//view.update(, this.occurrencesMonitor.getOccurrences());
				Thread.sleep(100);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		//view.update(this.wordsMonitor.getElaboratedWords(), this.occurrencesMonitor.getOccurrences());
		this.view.done();
	}
}
