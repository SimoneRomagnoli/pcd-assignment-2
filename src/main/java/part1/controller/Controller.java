package part1.controller;

import part1.model.ElaboratedWordsMonitor;
import part1.model.Model;
import part1.model.OccurrencesMonitor;
import part1.view.View;
import part1.view.Viewer;

import java.io.File;
import java.util.concurrent.ForkJoinPool;

/**
 * Controller part of the application - passive part.
 * 
 * @author aricci
 *
 */
public class Controller implements InputListener {

	public static final int N_THREADS = 11;

	private View view;
	private Flag stopFlag;
	
	public Controller(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public synchronized void started(File dir, File wordsFile, int limitWords) {
		this.stopFlag.reset();

		final ForkJoinPool executor = new ForkJoinPool(N_THREADS);

		final OccurrencesMonitor occurrencesMonitor = new OccurrencesMonitor(limitWords);
		final ElaboratedWordsMonitor wordsMonitor = new ElaboratedWordsMonitor();

		final Model model = new Model(this.stopFlag, dir, wordsFile, executor, occurrencesMonitor, wordsMonitor);
		final Viewer viewer = new Viewer(this.view, this.stopFlag, executor, occurrencesMonitor, wordsMonitor);

		executor.submit(model);
		executor.submit(viewer);
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

}
