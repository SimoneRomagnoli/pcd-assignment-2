package model;

import gui.ModelObserver;
import model.task.FilterCount;
import model.task.Split;
import model.task.Strip;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Class representing the model of the program:
 * here is nested the entire logic of the program.
 */
public class Model {

    private final List<ModelObserver> observers;
    private final Queue<File> documents;
    private final List<String> ignoredWords;
    private int limitWords;
    private boolean stopped;

    private ExecutorService executor;
    private final List<Future<String>> stripResults;
    private final List<Future<String[]>> splitResults;

    private final ElaboratedWordsMonitor wordsMonitor;
    private final OccurrencesMonitor occurrencesMonitor;

    public Model() {
        this.stopped = true;
        this.observers = new ArrayList<>();
        this.ignoredWords = new ArrayList<>();
        this.occurrencesMonitor = new OccurrencesMonitor();
        this.wordsMonitor = new ElaboratedWordsMonitor();
        this.documents = new ArrayDeque<>();

        this.stripResults = new LinkedList<>();
        this.splitResults = new LinkedList<>();
    }

    /**
     * Method called only by the master thread:
     * it handles the monitors in order to manage the workers
     * and their work.
     * @throws InterruptedException
     */
    public void update() throws IOException, ExecutionException, InterruptedException {
        if (!this.documents.isEmpty()){
            File f = documents.poll();
            PDDocument doc = PDDocument.load(f);
            AccessPermission ap = doc.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }
            try {
                Future<String> stripResult = executor.submit(new Strip(doc));
                stripResults.add(stripResult);
                System.out.println("Submitted file: " + f.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(Future<String> res: stripResults) {
            if(res.isDone()) {
                Future<String[]> splitResult = executor.submit(new Split(res.get()));
                splitResults.add(splitResult);
                stripResults.remove(res);
            }
        }

        for(Future<String[]> res:splitResults) {
            if(res.isDone()) {
                this.wordsMonitor.add(res.get().length);
                executor.submit(new FilterCount(res.get(), this.ignoredWords, this.occurrencesMonitor));
                splitResults.remove(res);
            }
        }

        notifyObservers();
    }

    /**
     * Setting main arguments of the program
     * when the computation starts.
     *
     * @param pdfDirectoryName
     * @param ignoredWordsFileName
     * @param limitWords
     * @throws IOException
     */
    public void setArgs(final String pdfDirectoryName, final String ignoredWordsFileName, final String limitWords) throws IOException {
        File pdfDirectory = new File(pdfDirectoryName);
        this.documents.addAll(Arrays.asList(Objects.requireNonNull(pdfDirectory.listFiles())));
        this.ignoredWords.addAll(Files.readAllLines(Path.of(ignoredWordsFileName)));
        this.limitWords = Integer.parseInt(limitWords);
    }

    /**
     * Method called by the controller to create
     * thread pool knowing the amount of available processors, cpu usage and number of documents.
     *
     * @param n
     * @throws IOException
     */
    public void createThreadPoolUpTo(final int n) {
        this.executor = Executors.newFixedThreadPool(Math.min(n, documents.size()));

    }

    public void addObserver(ModelObserver obs){ observers.add(obs); }

    /**
     * Notify the GUI sending the current values for most used words
     * and number of elaborated words
     */
    public void notifyObservers() {
        for (ModelObserver obs: observers) {
            Map<String, Integer> occurrences = this.occurrencesMonitor.getOccurrences();
            obs.modelUpdated(this.wordsMonitor.getElaboratedWords(),
                    occurrences.isEmpty()
                            ? Optional.empty()
                            : Optional.of(occurrences
                            .keySet()
                            .stream()
                            .sorted((a, b) -> occurrences.get(b) - occurrences.get(a))
                            .limit(this.limitWords)
                            .collect(Collectors.toMap(k -> k, occurrences::get))
                    ));
        }
    }

    public void start() {
        this.stopped = false;
    }

    public void stop() {
        this.stopped = true;
    }

    public boolean isStopped() {
        return this.isStopped();
    }

    public boolean isFinished() {
        return this.documents.isEmpty() && this.splitResults.isEmpty() && this.stripResults.isEmpty();
    }
}
