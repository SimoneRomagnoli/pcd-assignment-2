package part3.model;

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
    private static final int CHUNK_SIZE = 10;
    final static String REGEX = "[^a-zA-Z0-9]";
    private static List<String> ignoredWords;

    public FlowableOperations(List<String> ignoredWords) throws IOException {
        this.ignoredWords = ignoredWords;
    }

    public static void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + msg);
    }



    public static String loadAndStrip(File f ) throws IOException {
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
            return chunks;
        }
    }


    public static List<PDFTextStripper> loadAndGetStrippers(File f) throws IOException {
        List<PDFTextStripper> strippers = new ArrayList<>();
        PDDocument doc = PDDocument.load(f);
        AccessPermission ap = doc.getCurrentAccessPermission();
        log("stripping");
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        } else {
            for(int page = FIRST_PAGE, i=0; page <= doc.getNumberOfPages(); page+=CHUNK_SIZE) {
                strippers.add(new PDFTextStripper());
                strippers.get(i).setStartPage(page);
                strippers.get(i).setEndPage(Math.min(page+CHUNK_SIZE-1, doc.getNumberOfPages()));
                i++;
            }
            return strippers;
        }
    }

    public static String strip(PDFTextStripper stripper){
        return null;
    }

    public static String[] split(String page) {
        final String[] words = page.split(REGEX);


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
        log("counting");
        return occurrences;
    }
}
