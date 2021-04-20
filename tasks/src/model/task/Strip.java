package model.task;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.concurrent.Callable;

public class Strip implements Callable<String> {

    private static final int FIRST_PAGE = 1;
    private final PDDocument document;

    public Strip(PDDocument document) {
        this.document = document;
    }

    @Override
    public String call() throws Exception {
        final PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(FIRST_PAGE);
        stripper.setEndPage(this.document.getNumberOfPages());
        final String text = stripper.getText(this.document);
        this.document.close();
        return text;
    }
}
