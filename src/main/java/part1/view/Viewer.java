package part1.view;

import part1.model.ElaboratedWordsMonitor;
import part1.model.OccurrencesMonitor;
import part1.controller.Flag;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class Viewer extends RecursiveAction {

	private final ForkJoinPool executor;

	private final OccurrencesMonitor occurrencesMonitor;
	private final ElaboratedWordsMonitor wordsMonitor;
	private final View view;
	private final Flag done;
	
	public Viewer(View view, Flag done, ForkJoinPool executor, OccurrencesMonitor occurrencesMonitor, ElaboratedWordsMonitor wordsMonitor) {
		this.view = view;
		this.done = done;
		this.executor = executor;
		this.occurrencesMonitor = occurrencesMonitor;
		this.wordsMonitor = wordsMonitor;
	}

	@Override
	public void compute() {
		while (!done.isSet()) {
			try {
				view.update(this.wordsMonitor.getElaboratedWords(), this.occurrencesMonitor.getOccurrences());
				Thread.sleep(100);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		this.executor.shutdownNow();
		view.update(this.wordsMonitor.getElaboratedWords(), this.occurrencesMonitor.getOccurrences());
		this.view.done();
	}
}
