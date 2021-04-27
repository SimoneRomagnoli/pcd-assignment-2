package part3.controller;

import part3.model.Model;
import part3.view.View;
import part3.view.Viewer;

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
		System.out.println(dir.getAbsolutePath());
		this.model.setArgs(dir, wordsFile, limitWords);
		this.viewer = new Viewer(this.view, this.stopFlag);
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
			System.out.println("Here we should finish");
		});
	}

	public synchronized void stopped() {
		stopFlag.set();
	}

}
