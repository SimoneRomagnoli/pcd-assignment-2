package part3;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TestGuiPulling extends JFrame {

    Map<String, Integer> map = new HashMap<>();

    public TestGuiPulling(Flowable<File> stream){
        super("Swing + RxJava");
        setSize(150,60);
        setVisible(true);
        JTextArea text = new JTextArea();

        stream.flatMap(s -> Flowable.just(s)
                .subscribeOn(Schedulers.computation())
                .map(Operations::loadAndStrip)
                .map(Operations::split)
                .map(Operations::filter)
                .map(Operations::count)
                .subscribeOn(Schedulers.single())
                //perfetto, così facendo non
                // faccio fare niente alla gui che rimane reattiva
        ).subscribe(v -> {
            Operations.log("creating the map");
            v.forEach((s, c)-> {
                map.merge(s, c, Integer::sum);
            });

            text.setText("");

            map
                    .keySet()
                    .stream()
                    .sorted((a, b) -> map.get(b) - map.get(a))
                    .limit(5)
                    .collect(Collectors.toMap(k -> k, map::get))
                    .forEach((k,c)-> text.append(k + " " + c + "\n"));
        });
        getContentPane().add(text);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(-1);
            }
        });
    }

    public static void main(String[] args) {

        File directory = new File("/home/mr/Documents/Magistrale/pcd/Assignments/pcd-assignment-2/resMed");
        List<File> documents = Arrays.asList(Objects.requireNonNull(directory.listFiles()));

        System.out.println("File extracted form the directory, number of files = " + documents.size());

        Flowable<File> source = Flowable.fromIterable(documents);

        SwingUtilities.invokeLater(() -> {
            new TestGuiPulling(source);
        });

    }
}
