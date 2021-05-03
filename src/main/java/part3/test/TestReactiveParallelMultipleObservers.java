package part3.test;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class TestReactiveParallelMultipleObservers {

    final static String REGEX = "[^a-zA-Z0-9]";
    private static File ignoredWordsFile = new File("/home/mr/Documents/Magistrale/pcd/Assignments/pcd-assignment-2/ignoredWords.txt");
    private static List<String> ignoredWords = Collections.emptyList();


    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }

    static {
        try {
            ignoredWords = Files.readAllLines(ignoredWordsFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String loadAndStrip(File f ) throws IOException {
        PDDocument doc = PDDocument.load(f);
        AccessPermission ap = doc.getCurrentAccessPermission();
        log("stripping");
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        } else {
            final PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }

    private static String[] split(String page) {
        return page.split(REGEX);
    }

    private static List<String> filter(String[] splittedText) {
        return Arrays.stream(splittedText).filter(w -> !ignoredWords.contains(w)).collect(Collectors.toList());
    }

    private static Map<String, Integer> count(List<String> words) {
        Map<String, Integer> occurrences = new HashMap<>();
        for(String word: words) {
            if(occurrences.containsKey(word)) {
                occurrences.replace(word, occurrences.get(word)+1);
            } else {
                occurrences.put(word, 1);
            }
        }
        log("counting");
        return occurrences;
    }


    // il problema di questo è che dovrebbe mergiare i documenti, mentre lui crea una mappa per ogni file


    public static void main(String[] args) throws IOException {

        Map<String, Integer> map = new HashMap<>();


        File directory = new File("/home/mr/Documents/Magistrale/pcd/Assignments/pcd-assignment-2/res");
        List<File> documents = Arrays.asList(Objects.requireNonNull(directory.listFiles()));

        System.out.println("File extracted form the directory, number of files = " +  documents.size());

        Flowable<File> source = Flowable.fromIterable(documents);

        source.flatMap(s -> Flowable.just(s)
                .observeOn(Schedulers.io())
                .map(TestReactiveParallelMultipleObservers::loadAndStrip)
                .observeOn(Schedulers.computation())
                .map(TestReactiveParallelMultipleObservers::split)
                .map(TestReactiveParallelMultipleObservers::filter)
                .map(TestReactiveParallelMultipleObservers::count)
          //.sorted()    // qui vorrei ordinare in base al conteggio delle parole
          //.limit(1)  // questa sarebbe da fare ma è un metodo degli stream, l'alternativa è take
        ).blockingSubscribe(v -> {

            v.forEach((s, c)-> {
                map.merge(s, c, Integer::sum);
            });
        });

        System.out.println("resulting map: ");
        map
        .keySet()
        .stream()
        .sorted((a, b) -> map.get(b) - map.get(a))
        .limit(5)
        .collect(Collectors.toMap(k -> k, map::get))
                .forEach((k,v)-> log(k + " " + v));
    }
}
