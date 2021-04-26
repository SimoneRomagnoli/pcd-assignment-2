package controller;

import model.Model;
import view.View;
import view.Viewer;

import java.io.File;

/**
 * Controller part of the application - passive part.
 * 
 * @author aricci
 *
 */
public class Controller implements InputListener {


	public static final int N_THREADS = 8;

	private View view;
	private Flag stopFlag;
	private Viewer viewer;
	private Model model;
	
	public Controller(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public synchronized void started(File dir, File wordsFile, int limitWords) {
		stopFlag.reset();
		this.model = new Model(this.stopFlag);
		this.model.setArgs(dir, wordsFile, limitWords);
		this.viewer = new Viewer(this.view, this.stopFlag);
		this.viewer.setOccurrencesMonitor(this.model.getOccurrencesMonitor());
		this.viewer.setElaboratedWordMonitor(this.model.getElaboratedWordsMonitor());
		this.model.createThreadPool(N_THREADS);
		this.model.start();
		this.viewer.start();
		new Thread(() -> {
			while(!stopFlag.isSet()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.model.cancelAll();
		});
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

}
