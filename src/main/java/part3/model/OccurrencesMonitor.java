package part3.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Monitor that keeps the count of occurrences of words.
 */
public class OccurrencesMonitor {

    private final int limit;
    private Map<String, Integer> occurrences;

    public OccurrencesMonitor(final int limitWords) {
        this.limit = limitWords;
        this.occurrences = new HashMap<>();
    }

    public synchronized void writeOccurrence(Map<String, Integer> occ) {
        occ.keySet().forEach(k -> {
            if(occurrences.containsKey(k)) {
                occurrences.replace(k, this.occurrences.get(k)+occ.get(k));
            } else {
                occurrences.put(k, occ.get(k));
            }
        });
    }

    public synchronized Map<String, Integer> getOccurrences() {
        return this.occurrences
                .keySet()
                .stream()
                .sorted((a, b) -> occurrences.get(b) - occurrences.get(a))
                .limit(this.limit)
                .collect(Collectors.toMap(k -> k, occurrences::get));
    }
}
