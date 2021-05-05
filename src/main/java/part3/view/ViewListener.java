package part3.v2.view;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import part3.v2.flow.Flow;
import part3.v2.flow.FlowableOperations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing the view part of the application.
 *
 */
public class ViewListener implements InputListener {

	private View frame;
	private Map<String, Integer> map;
	private Map<String, Integer> prevTop;
	private Flowable<Map<String, Integer>> flow;
	private int elaboratedWords;
	private Disposable disposable;
	private FlowableOperations operations;


	public ViewListener(){
		frame = new View();
		this.frame.setListener(this);
		this.map = new HashMap<>();
		this.prevTop = new HashMap<>();
	}

	@Override
	public void start(File dir, File wordsFile, int limitWords) throws IOException {
		this.map = new HashMap<>();
		this.prevTop = new HashMap<>();
		this.operations = new FlowableOperations(wordsFile, limitWords);
		this.flow = new Flow(dir).getMapsFlowable();
		this.disposable = flow.subscribe(
				localMap -> {
					FlowableOperations.log("creating the map");
					localMap.forEach((s, c)-> {
						map.merge(s, c, Integer::sum);
						this.elaboratedWords = map.values().stream().reduce(Integer::sum).get();
						if(!FlowableOperations.getTop(map).equals(prevTop)){
							prevTop = FlowableOperations.getTop(map);
							frame.update(elaboratedWords, prevTop);
						}
					});
				}, error -> {
					part3.model.FlowableOperations.log("an error occurred" + error.getMessage());
				},
				() -> {
					FlowableOperations.log("DONE");
					frame.done();
				});
	}

	@Override
	public void stop(){
		disposable.dispose();
	}

	public void display() {
        javax.swing.SwingUtilities.invokeLater(() -> {
        	frame.setVisible(true);
        });
    }
}
	
