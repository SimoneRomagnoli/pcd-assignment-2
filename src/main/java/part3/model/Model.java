package part3.model;

import io.reactivex.rxjava3.core.Observable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import part3.controller.Flag;
import part3.model.task.Strip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * Class representing the model of the program:
 * here is nested the entire logic of the program.
 */
public class Model extends Thread {

    private final Queue<File> documents;
    private final List<String> ignoredWords;


    private Flag flag;


    public Model(Flag flag) {
        this.ignoredWords = new ArrayList<>();
        this.flag = flag;
        this.documents = new ArrayDeque<>();
    }

    /**
     * Method called at the beginning of computation:
     * starts the main tasks via Executors.
     */
    public void run() {
        final long start = System.currentTimeMillis();


       // stream di pdDocument -> stream di text -> filtrato -> mappato -> contato -> selezionato i primi n args

        Observable<File> src = Observable.fromIterable(documents);

        src.map(File::getName).subscribe(System.out::println);

//        //Execute Strip tasks
//        while(!this.documents.isEmpty() && !this.flag.isSet()) {
//            try {
//                File f = documents.poll();
//                PDDocument doc = PDDocument.load(f);
//                AccessPermission ap = doc.getCurrentAccessPermission();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        //Wait for task termination and then stop the application
//        for(Future<Void> result:results) {
//            try {
//                result.get();
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        this.executor.shutdown();
//        this.flag.set();
        System.out.println("Time elapsed: "+(System.currentTimeMillis()-start)+" ms.");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
