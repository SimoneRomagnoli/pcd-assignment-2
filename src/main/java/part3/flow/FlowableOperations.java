package part3.flow;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class FlowableOperations {

    private static final int FIRST_PAGE = 1;
    final static String REGEX = "[^a-zA-Z0-9]";
    private static final List<String> ignoredWords = new ArrayList<>();
    private static int limitWords;

    public FlowableOperations(File wordsFile, Integer limitWords) throws IOException {
        ignoredWords.addAll(Files.readAllLines(wordsFile.toPath()));
        this.limitWords = limitWords;
    }

    public static void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }

    public static List<String> loadAndGetChuncks(File f ) throws IOException {
        List<String> chunks = new ArrayList<>();
        System.out.println("Stripping in chunk" + f.getName());
        PDDocument doc = PDDocument.load(f);
        AccessPermission ap = doc.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        } else {

            final PDFTextStripper stripper = new PDFTextStripper();
            int nPages = doc.getNumberOfPages();
            if(nPages==1){
                chunks.add(stripper.getText(doc));
            }
            else{
                for (int i = FIRST_PAGE; i <= nPages; i++) {
                    stripper.setStartPage(i);
                    stripper.setEndPage(i);
                    String chunk =  stripper.getText(doc);
                    chunks.add(chunk);
                }
            }
            doc.close();
            return chunks;
        }
    }

    public static String[] split(String page) {
        return page.split(REGEX);
    }

    public static List<String> filter(String[] splittedText) {
        return Arrays.stream(splittedText).filter(w -> !ignoredWords.contains(w)).collect(Collectors.toList());
    }

    public static Map<String, Integer> count(List<String> words) {
        Map<String, Integer> occurrences = new HashMap<>();
        for(String word: words) {
            if(occurrences.containsKey(word)) {
                occurrences.replace(word, occurrences.get(word)+1);
            } else {
                occurrences.put(word, 1);
            }
        }
        return occurrences;
    }


    public static Map<String, Integer> getTop(Map<String, Integer> map){
        return map.keySet()
                .stream()
                .sorted((a, b) -> map.get(b) - map.get(a))
                .limit(limitWords)
                .collect(Collectors.toMap(k -> k, map::get));
    }


}
