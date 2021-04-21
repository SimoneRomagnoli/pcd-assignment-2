package controller;

import model.Model;
import view.View;

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
	private View view;
	private Model model;
	
	public Controller(View view){
		this.stopFlag = new Flag();
		this.view = view;
	}
	
	public synchronized void started(File dir, File wordsFile, int limitWords) {
		stopFlag.reset();
		this.model = new Model(this.stopFlag);
		this.model.setArgs(dir, wordsFile, limitWords);
		this.model.createThreadPoolUpTo(N_THREADS);
		this.model.start();
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

}
