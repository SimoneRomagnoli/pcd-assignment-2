package model;

import controller.Flag;
import model.task.SplitFilterCount;
import model.task.Strip;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class representing the model of the program:
 * here is nested the entire logic of the program.
 */
public class Model extends Thread {

    private final Queue<File> documents;
    private final List<String> ignoredWords;

    private ExecutorService executor;
    private final List<Future<Void>> results;

    private Flag flag;
    private ElaboratedWordsMonitor wordsMonitor;
    private OccurrencesMonitor occurrencesMonitor;

    public Model(Flag flag) {
        this.ignoredWords = new ArrayList<>();
        this.flag = flag;
        this.documents = new ArrayDeque<>();
        this.results = new LinkedList<>();
    }

    /**
     * Method called at the beginning of computation:
     * starts the main tasks via Executors.
     */
    public void run() {
        //Execute Strip tasks
        while(!this.documents.isEmpty() && !this.flag.isSet()) {
            try {
                File f = documents.poll();
                PDDocument doc = PDDocument.load(f);
                AccessPermission ap = doc.getCurrentAccessPermission();
                if (!ap.canExtractContent()) {
                    throw new IOException("You do not have permission to extract text");
                } else {
                    Future<Void> result = executor.submit(new Strip(doc, this.executor, this.ignoredWords, this.occurrencesMonitor, this.wordsMonitor));
                    this.results.add(result);
                    System.out.println("Submitted file: " + f.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Wait for task termination and then stop the application
        for(Future<Void> result:results) {
            try {
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        this.executor.shutdown();
        this.flag.set();
        System.out.println("End");
    }

    /**
     * Setting main arguments of the program
     * when the computation starts.
     *
     * @param pdfDirectory
     * @param ignoredWordsFile
     * @param limitWords
     * @throws IOException
     */
    public void setArgs(final File pdfDirectory, final File ignoredWordsFile, final int limitWords) {
        try {
            this.documents.addAll(Arrays.asList(Objects.requireNonNull(pdfDirectory.listFiles())));
            this.ignoredWords.addAll(Files.readAllLines(ignoredWordsFile.toPath()));
            this.occurrencesMonitor = new OccurrencesMonitor(limitWords);
            this.wordsMonitor = new ElaboratedWordsMonitor();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void cancelAll() {
        this.executor.shutdownNow();
        /*
        for(Future<Void> f:results) {
            f.cancel(true);
        }
        */
    }

    public OccurrencesMonitor getOccurrencesMonitor() {
        return this.occurrencesMonitor;
    }

    public ElaboratedWordsMonitor getElaboratedWordsMonitor() {
        return this.wordsMonitor;
    }
}
