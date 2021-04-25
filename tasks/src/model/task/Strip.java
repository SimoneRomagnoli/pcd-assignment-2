package model.task;

import model.ElaboratedWordsMonitor;
import model.OccurrencesMonitor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class Strip implements Callable<Void> {

    private static final int FIRST_PAGE = 1;
    private final PDDocument document;
    private final ExecutorService executor;
    private final List<String> ignoredWords;
    private final OccurrencesMonitor occurrencesMonitor;
    private final ElaboratedWordsMonitor wordsMonitor;

    public Strip(PDDocument document, ExecutorService executor, List<String> ignoredWords, OccurrencesMonitor occurrencesMonitor, ElaboratedWordsMonitor wordsMonitor) {
        this.document = document;
        this.executor = executor;
        this.ignoredWords = new ArrayList<>(ignoredWords);
        this.occurrencesMonitor = occurrencesMonitor;
        this.wordsMonitor = wordsMonitor;
    }

    @Override
    public Void call() throws Exception {
        final PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(FIRST_PAGE);
        stripper.setEndPage(this.document.getNumberOfPages());
        final String text = stripper.getText(this.document);
        this.executor.submit(new SplitFilterCount(text, this.ignoredWords, this.occurrencesMonitor, this.wordsMonitor));
        this.document.close();
        return null;
    }
}
