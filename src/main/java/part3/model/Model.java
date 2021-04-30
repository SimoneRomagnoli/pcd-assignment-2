package part3.model;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import part3.controller.Controller;
import part3.model.Operations;
import part3.controller.Flag;

import javax.swing.*;
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
    private Operations operations;
    Map<String, Integer> map;


    private Flag flag;
    private int limitWords;

    public Model(Flag flag, Controller controller) {
        this.controller = controller;
        this.map = new HashMap<>();
        this.ignoredWords = new ArrayList<>();
        this.flag = flag;
        this.documents = new ArrayList<>();
    }

    /**
     * Method called at the beginning of computation:
     * starts the main tasks via Executors.
     */
    public void go() {

        final long start = System.currentTimeMillis();

        Flowable<File> source = Flowable.fromIterable(documents);

        source.flatMap(s->Flowable.just(s)
                .subscribeOn(Schedulers.computation())
                //.map(Operations::loadAndStrip)
                .map(Operations::loadAndGetChuncks)
                .flatMap(Flowable::fromIterable)
                .map(Operations::split)
                .map(Operations::filter)
                .map(Operations::count)
        ).subscribe(v -> {
            Operations.log("creating the map");
            v.forEach((s, c)-> {
                map.merge(s, c, Integer::sum);
                controller.update(getElaboratedWords(), getTop());
            });
        });

        //this.flag.set();
        System.out.println("Time elapsed: "+(System.currentTimeMillis()-start)+" ms.");
    }

    public synchronized Map<String, Integer> getTop(){
        System.out.println("updating the view");
        return map.keySet()
                .stream()
                .sorted((a, b) -> map.get(b) - map.get(a))
                .limit(limitWords)
                .collect(Collectors.toMap(k -> k, map::get));
    }

    public synchronized int getElaboratedWords(){
        return 10; //da cambiare ovviamente
    }

    /**
     * Setting main arguments of the program
     * when the computation starts.
     *
     * @param pdfDirectory
     * @param ignoredWordsFile     * @param limitWords
     * @throws IOException
     */
    public void setArgs(final File pdfDirectory, final File ignoredWordsFile, final int limitWords) {
        try {
            this.documents.addAll(Arrays.asList(Objects.requireNonNull(pdfDirectory.listFiles())));
            this.ignoredWords.addAll(Files.readAllLines(ignoredWordsFile.toPath()));
            this.limitWords = limitWords;
            this.operations = new Operations(ignoredWords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
