package part3.model;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import part3.controller.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing the model of the program:
 * here is nested the entire logic of the program.
 */
public class Model{

    private final List<File> documents;
    private final List<String> ignoredWords;
    private final Controller controller;
    private FlowableOperations operations;
    Map<String, Integer> map;
    Map<String, Integer> beforeTop;
    private Disposable disposable;
    private int limitWords;



    public Model(Controller controller, File dir, File wordsFile, int limitWords) {
        this.controller = controller;
        this.map = new HashMap<>();
        this.ignoredWords = new ArrayList<>();
        this.documents = new ArrayList<>();
        try {
            this.documents.addAll(Arrays.asList(Objects.requireNonNull(dir.listFiles())));
            this.ignoredWords.addAll(Files.readAllLines(wordsFile.toPath()));
            this.limitWords = limitWords;
            this.operations = new FlowableOperations(ignoredWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called at the beginning of computation:
     * starts the main tasks via Executors.
     */
    public void startFlowableComputation() {

        Flowable<File> source = Flowable.fromIterable(documents);

       disposable = source.flatMap(s->Flowable.just(s)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                //.map(Operations::loadAndStrip)
                .map(FlowableOperations::loadAndGetChuncks)
                .flatMap(Flowable::fromIterable)
               .observeOn(Schedulers.computation())
                .map(FlowableOperations::split)
                .map(FlowableOperations::filter)
                .map(FlowableOperations::count)
       ).subscribe(localMap -> {
            FlowableOperations.log("creating the map");
            localMap.forEach((s, c)-> {
                map.merge(s, c, Integer::sum);
                if(!getTop().equals(beforeTop)){
                    beforeTop = getTop();
                    controller.update(getElaboratedWords(), getTop());
                }
            });
        }, error -> {
                   FlowableOperations.log("an error occurred" + error.getMessage());
               },
               () -> {
                    controller.done();
               });
    }


    public synchronized Map<String, Integer> getTop(){
        return map.keySet()
                .stream()
                .sorted((a, b) -> map.get(b) - map.get(a))
                .limit(limitWords)
                .collect(Collectors.toMap(k -> k, map::get));
    }

    public synchronized int getElaboratedWords(){
        return 10; //da cambiare ovviamente
    }


    public void stop() {
        disposable.dispose();
    }
}
