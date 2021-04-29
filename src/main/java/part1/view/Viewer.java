package part1.view;

import part1.model.ElaboratedWordsMonitor;
import part1.model.OccurrencesMonitor;
import part1.controller.Flag;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Viewer extends RecursiveTask<Void> {

	private final ForkJoinPool executor;

	private OccurrencesMonitor occurrencesMonitor;
	private ElaboratedWordsMonitor wordsMonitor;
	private View view;
	private Flag done;
	
	public Viewer(View view, Flag done, ForkJoinPool executor) {
		this.view = view;
		this.done = done;
		this.executor = executor;
	}

	@Override
	public Void compute() {
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
		return null;
	}

	public void setOccurrencesMonitor(OccurrencesMonitor monitor) {
		this.occurrencesMonitor = monitor;
	}

	public void setElaboratedWordMonitor(ElaboratedWordsMonitor monitor) {
		this.wordsMonitor = monitor;
	}
}
