package part1.model;

import part1.controller.Flag;
import part1.model.task.Strip;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

/**
 * Class representing the model of the program:
 * here is nested the entire logic of the program.
 */
public class Model extends RecursiveTask<Void> {

    private final Queue<File> documents;
    private final List<String> ignoredWords;


    private final List<RecursiveAction> forks;
    private final ForkJoinPool executor;

    private Flag flag;
    private ElaboratedWordsMonitor wordsMonitor;
    private OccurrencesMonitor occurrencesMonitor;

    public Model(final Flag flag, final File pdfDirectory, final File ignoredWordsFile, final ForkJoinPool executor, final OccurrencesMonitor occurrencesMonitor, final ElaboratedWordsMonitor wordsMonitor) {
        this.ignoredWords = new ArrayList<>();
        this.flag = flag;
        this.documents = new ArrayDeque<>();
        this.forks = new LinkedList<>();
        this.executor = executor;

        try {
            this.documents.addAll(Arrays.asList(Objects.requireNonNull(pdfDirectory.listFiles())));
            this.ignoredWords.addAll(Files.readAllLines(ignoredWordsFile.toPath()));
            this.occurrencesMonitor = occurrencesMonitor;
            this.wordsMonitor = wordsMonitor;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called at the beginning of computation:
     * starts the main tasks via Executors.
     */
    @Override
    public Void compute() {
        final Long start = System.currentTimeMillis();

        //Execute Strip tasks
        while(!this.documents.isEmpty() && !this.flag.isSet()) {
            try {
                File f = documents.poll();
                PDDocument doc = PDDocument.load(f);
                AccessPermission ap = doc.getCurrentAccessPermission();
                if (!ap.canExtractContent()) {
                    throw new IOException("You do not have permission to extract text");
                } else {
                    RecursiveAction strip = new Strip(doc, this.ignoredWords, this.occurrencesMonitor, this.wordsMonitor);
                    this.forks.add(strip);
                    strip.fork();
                    System.out.println("Submitted file: " + f.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Wait strip join before shutting down
        for(RecursiveAction fork: forks) {
            fork.join();
        }

        this.flag.set();

        System.out.println("Time elapsed: "+(System.currentTimeMillis()-start)+" ms.");

        return null;
    }

    public OccurrencesMonitor getOccurrencesMonitor() {
        return this.occurrencesMonitor;
    }

    public ElaboratedWordsMonitor getElaboratedWordsMonitor() {
        return this.wordsMonitor;
    }
}
