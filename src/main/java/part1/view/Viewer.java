package part1.view;

import part1.model.ElaboratedWordsMonitor;
import part1.model.OccurrencesMonitor;
import part1.controller.Flag;

public class Viewer extends Thread {

	private OccurrencesMonitor occurrencesMonitor;
	private ElaboratedWordsMonitor wordsMonitor;
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
				view.update(this.wordsMonitor.getElaboratedWords(), this.occurrencesMonitor.getOccurrences());
				Thread.sleep(100);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		view.update(this.wordsMonitor.getElaboratedWords(), this.occurrencesMonitor.getOccurrences());
		this.view.done();
	}

	public void setOccurrencesMonitor(OccurrencesMonitor monitor) {
		this.occurrencesMonitor = monitor;
	}

	public void setElaboratedWordMonitor(ElaboratedWordsMonitor monitor) {
		this.wordsMonitor = monitor;
	}
}
