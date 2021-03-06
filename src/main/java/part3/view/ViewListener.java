package part3.view;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import part3.flow.Flow;
import part3.flow.FlowableOperations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * View input listener and subscriber of the flow.
 *
 */
public class ViewListener implements InputListener {

	private final View view;
	private Map<String, Integer> map;
	private Map<String, Integer> prevTop;
	private int elaboratedWords;
	private Disposable disposable;

	public ViewListener(){
		view = new View();
		this.view.setListener(this);
	}

	@Override
	public void start(File dir, File wordsFile, int limitWords){
		final long start = System.currentTimeMillis();
		this.map = new HashMap<>();
		this.prevTop = new HashMap<>();
		try {
			FlowableOperations.setArgs(wordsFile, limitWords);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Flowable<Map<String, Integer>> flow = new Flow(dir).getMapsFlowable();

		this.disposable = flow.subscribe(
				localMap -> {
					localMap.forEach((s, c)-> {
						map.merge(s, c, Integer::sum);
						this.elaboratedWords = map.values().stream().reduce(Integer::sum).get();
					});
					updateGuiIfNecessary();
				}, error -> {
						FlowableOperations.log("an error occurred" + error.getMessage());
				}, () -> {
						FlowableOperations.log("DONE");
						view.done();
						System.out.println("Time elapsed: "+(System.currentTimeMillis()-start)+" ms.");
				});
	}

	private void updateGuiIfNecessary(){
		if(!FlowableOperations.getTop(map).equals(prevTop)){
			prevTop = FlowableOperations.getTop(map);
			view.update(elaboratedWords, prevTop);
		}
	}

	@Override
	public void stop(){
		disposable.dispose();
	}

	public void display() {
        javax.swing.SwingUtilities.invokeLater(() -> {
        	view.setVisible(true);
        });
    }
}
	
