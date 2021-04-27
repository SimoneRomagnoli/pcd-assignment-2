package part3.model.task;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import part3.model.ElaboratedWordsMonitor;
import part3.model.OccurrencesMonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Strip extends RecursiveTask<Void> {

    private static final int FIRST_PAGE = 1;
    private static final int CHUNK_SIZE = 10;

    private final PDDocument document;
    private final List<String> ignoredWords;

    private final OccurrencesMonitor occurrencesMonitor;
    private final ElaboratedWordsMonitor wordsMonitor;

    public Strip(PDDocument document, List<String> ignoredWords, OccurrencesMonitor occurrencesMonitor, ElaboratedWordsMonitor wordsMonitor) {
        this.document = document;
        this.ignoredWords = new ArrayList<>(ignoredWords);
        this.occurrencesMonitor = occurrencesMonitor;
        this.wordsMonitor = wordsMonitor;
    }

    @Override
    public Void compute() {
        try {
            final PDFTextStripper stripper = new PDFTextStripper();
            for(int page = FIRST_PAGE; page <= this.document.getNumberOfPages(); page+=CHUNK_SIZE) {
                stripper.setStartPage(page);
                stripper.setEndPage(Math.min(page+CHUNK_SIZE-1, document.getNumberOfPages()));
                final String text = stripper.getText(this.document);
                SplitFilterCount spf = new SplitFilterCount(text, this.ignoredWords, this.occurrencesMonitor, this.wordsMonitor);
                spf.fork();
                spf.join();
            }
            this.document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
