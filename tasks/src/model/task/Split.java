package model.task;

import java.util.concurrent.Callable;

public class Split implements Callable<String[]> {

    private static final String REGEX = "[^a-zA-Z0-9]";
    private final String text;

    public Split(String text) {
        this.text = text;
    }

    @Override
    public String[] call() {
        return split(this.text);
    }

    private String[] split(String page) {
        return page.split(REGEX);
    }
}
