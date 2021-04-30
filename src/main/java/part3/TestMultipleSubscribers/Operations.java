package part3.TestMultipleSubscribers;

import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import part1.model.task.SplitFilterCount;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Operations {

    private static final int FIRST_PAGE = 1;
    private static final int CHUNK_SIZE = 10;
    final static String REGEX = "[^a-zA-Z0-9]";
    private static File ignoredWordsFile = new File("/home/mr/Documents/Magistrale/pcd/Assignments/pcd-assignment-2/ignoredWords.txt");
    private static List<String> ignoredWords;

    static {
        try {
            ignoredWords = Files.readAllLines(ignoredWordsFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Operations() throws IOException {
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
            for (int i = FIRST_PAGE; i < nPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String chunk =  stripper.getText(doc);
                chunks.add(chunk);
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
