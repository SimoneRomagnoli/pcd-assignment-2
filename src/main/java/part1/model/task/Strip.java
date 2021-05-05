package part1.model.task;

import part1.model.ElaboratedWordsMonitor;
import part1.model.OccurrencesMonitor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class Strip extends RecursiveAction {

    private static final int FIRST_PAGE = 1;
    private static final int CHUNK_SIZE = 1;

    private final PDDocument document;
    private final List<String> ignoredWords;

    private final OccurrencesMonitor occurrencesMonitor;
    private final ElaboratedWordsMonitor wordsMonitor;

    private final List<RecursiveAction> forks;

    public Strip(PDDocument document, List<String> ignoredWords, OccurrencesMonitor occurrencesMonitor, ElaboratedWordsMonitor wordsMonitor) {
        this.document = document;
        this.ignoredWords = new ArrayList<>(ignoredWords);
        this.occurrencesMonitor = occurrencesMonitor;
        this.wordsMonitor = wordsMonitor;
        this.forks = new ArrayList<>();
    }

    @Override
    public void compute() {
        try {
            final PDFTextStripper stripper = new PDFTextStripper();
            for(int page = FIRST_PAGE; page <= this.document.getNumberOfPages(); page+=CHUNK_SIZE) {
                stripper.setStartPage(page);
                stripper.setEndPage(Math.min(page+CHUNK_SIZE-1, document.getNumberOfPages()));
                final String text = stripper.getText(this.document);
                SplitFilterCount spf = new SplitFilterCount(text, this.ignoredWords, this.occurrencesMonitor, this.wordsMonitor);
                this.forks.add(spf);
                spf.fork();
            }

            this.document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(RecursiveAction fork:forks) {
            fork.join();
        }
    }
}
