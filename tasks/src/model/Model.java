package model;

import controller.Flag;
import model.task.FilterCount;
import model.task.Split;
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
    private final List<Future<String>> stripResults;
    private final List<Future<String[]>> splitResults;

    private Flag flag;
    private ElaboratedWordsMonitor wordsMonitor;
    private OccurrencesMonitor occurrencesMonitor;

    public Model(Flag flag) {
        this.ignoredWords = new ArrayList<>();
        this.flag = flag;
        this.documents = new ArrayDeque<>();

        this.stripResults = new LinkedList<>();
        this.splitResults = new LinkedList<>();
    }

    /**
     * Method called at the beginning of computation:
     * starts the main tasks via Executors.
     */
    public void run() {
        if (!this.documents.isEmpty()){
            File f = documents.poll();
            PDDocument doc = null;
            try {
                doc = PDDocument.load(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            AccessPermission ap = doc.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                try {
                    throw new IOException("You do not have permission to extract text");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Future<String> stripResult = executor.submit(new Strip(doc));
                stripResults.add(stripResult);
                System.out.println("Submitted file: " + f.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {

            final Iterator<Future<String>> stripIterator = stripResults.iterator();
            while (stripIterator.hasNext()) {
                final Future<String> res = stripIterator.next();
                if (res.isDone()) {
                    Future<String[]> splitResult = null;
                    splitResult = executor.submit(new Split(res.get()));
                    splitResults.add(splitResult);
                    stripIterator.remove();
                }
            }

            final Iterator<Future<String[]>> splitIterator = splitResults.iterator();
            while (splitIterator.hasNext()) {
                final Future<String[]> res = splitIterator.next();
                if (res.isDone()) {
                    this.wordsMonitor.add(res.get().length);
                    executor.submit(new FilterCount(res.get(), this.ignoredWords, this.occurrencesMonitor));
                    splitIterator.remove();
                }
            }
        }  catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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
}
