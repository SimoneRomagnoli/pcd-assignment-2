package model.task;

import model.OccurrencesMonitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class FilterCount implements Callable<Void> {

    private final String[] text;
    private final List<String> ignoredWords;
    private final OccurrencesMonitor monitor;

    public FilterCount(String[] text, List<String> ignoredWords, OccurrencesMonitor monitor) {
        this.text = text;
        this.ignoredWords = ignoredWords;
        this.monitor = monitor;
    }

    @Override
    public Void call() {
        this.monitor.writeOccurrence(count(filter(this.text)));
        return null;
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
