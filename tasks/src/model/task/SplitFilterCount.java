package model.task;

import model.ElaboratedWordsMonitor;
import model.OccurrencesMonitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class SplitFilterCount implements Callable<Void> {

    private static final String REGEX = "[^a-zA-Z0-9]";

    private final String text;
    private final List<String> ignoredWords;
    private final OccurrencesMonitor occurrencesMonitor;
    private final ElaboratedWordsMonitor wordsMonitor;

    public SplitFilterCount(String text, List<String> ignoredWords, OccurrencesMonitor occurrencesMonitor, ElaboratedWordsMonitor wordsMonitor) {
        this.text = text;
        this.ignoredWords = ignoredWords;
        this.occurrencesMonitor = occurrencesMonitor;
        this.wordsMonitor = wordsMonitor;
    }

    @Override
    public Void call() {
        String[] splitText = split(this.text);
        this.wordsMonitor.add(splitText.length);
        this.occurrencesMonitor.writeOccurrence(count(filter(splitText)));
        return null;
    }

    private String[] split(String page) {
        return page.split(REGEX);
    }

    private List<String> filter(String[] splittedText) {
        return Arrays.stream(splittedText).filter(w -> !this.ignoredWords.contains(w)).collect(Collectors.toList());
    }

    private Map<String, Integer> count(List<String> words) {
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
}
