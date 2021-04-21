package view;

import model.ElaboratedWordsMonitor;
import model.OccurrencesMonitor;
import controller.Flag;

public class Viewer extends Thread {

	private OccurrencesMonitor occurrencesMonitor;
	private ElaboratedWordsMonitor wordsMonitor;
	private View view;
	private Flag done;
	
	public Viewer(OccurrencesMonitor occurrencesMonitor, ElaboratedWordsMonitor wordsMonitor, View view, Flag done) {
		super("viewer");
		this.occurrencesMonitor = occurrencesMonitor;
		this.wordsMonitor = wordsMonitor;
		this.view = view;
		this.done = done;
	}

	public void run() {
		while (!done.isSet()) {
			try {
				view.update(this.wordsMonitor.getElaboratedWords(), this.occurrencesMonitor.getOccurrences());
				Thread.sleep(10);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		view.update(this.wordsMonitor.getElaboratedWords(), this.occurrencesMonitor.getOccurrences());
	}
}
