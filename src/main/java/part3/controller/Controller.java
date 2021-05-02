package part3.controller;

import part3.model.Model;
import part3.view.View;

import java.io.File;
import java.util.Map;

/**
 * Controller part of the application - passive part.
 * 
 * @author aricci
 *
 */
public class Controller implements InputListener {

	private View view;
	private Model model;
	
	public Controller(View view){
		this.view = view;
	}
	
	public synchronized void started(File dir, File wordsFile, int limitWords) {
		this.model = new Model(this, dir, wordsFile, limitWords);
		this.model.startFlowableComputation();
	}

	public synchronized void stopped() {
		this.model.stop();
	}

	public void update( int elaboratedWords, Map<String, Integer> top) {
		view.update(elaboratedWords,top);
	}

	public void done() {
		this.view.done();
	}
}
