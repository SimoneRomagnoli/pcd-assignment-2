package controller;

import model.Model;
import view.View;
import view.Viewer;

import java.io.File;
import java.io.IOException;

/**
 * Controller part of the application - passive part.
 * 
 * @author aricci
 *
 */
public class Controller implements InputListener {

	public static final int N_CPU = Runtime.getRuntime().availableProcessors();
	public static final double U_CPU = 0.8;
	public static final double WAIT_COMPUTE_RATIO = 0.85;
	public static final int N_THREADS =  (int)(N_CPU * U_CPU * (1 + WAIT_COMPUTE_RATIO));

	private Flag stopFlag;
	private Viewer viewer;
	private Model model;
	
	public Controller(View view){
		this.stopFlag = new Flag();
		this.viewer = new Viewer(view, this.stopFlag);
	}
	
	public synchronized void started(File dir, File wordsFile, int limitWords) {
		stopFlag.reset();
		this.model = new Model(this.stopFlag);
		this.model.setArgs(dir, wordsFile, limitWords);
		this.viewer.setOccurrencesMonitor(this.model.getOccurrencesMonitor());
		this.viewer.setElaboratedWordMonitor(this.model.getElaboratedWordsMonitor());
		this.model.createThreadPoolUpTo(N_THREADS);
		this.model.start();
		System.out.println("Model started");
		this.viewer.start();
		System.out.println("Viewer started");
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
