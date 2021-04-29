package part3;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import part3.TestMultipleSubscribers.Operations;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TestGuiPushed extends JFrame {
    JTextArea text ;

    public TestGuiPushed() {
        super("Swing + RxJava");
        setSize(150, 60);
        setVisible(true);
        text = new JTextArea();


        getContentPane().add(text);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

        public void update (Map < String, Integer > map){
            text.setText("");
            map
                    .keySet()
                    .stream()
                    .sorted((a, b) -> map.get(b) - map.get(a))
                    .limit(5)
                    .collect(Collectors.toMap(k -> k, map::get))
                    .forEach((k, c) -> text.append(k + " " + c + "\n"));

        }


    public static void main(String[] args) {


        Map<String, Integer> map = new HashMap<>();
        TestGuiPushed gui = new TestGuiPushed();


        File directory = new File("/home/mr/Documents/Magistrale/pcd/Assignments/pcd-assignment-2/res");
        List<File> documents = Arrays.asList(Objects.requireNonNull(directory.listFiles()));


        System.out.println("File extracted form the directory, number of files = " + documents.size());

        Flowable<File> source = Flowable.fromIterable(documents);


        source.flatMap(s -> Flowable.just(s)
                        .subscribeOn(Schedulers.computation())
                        .map(Operations::loadAndStrip)
                        .map(Operations::split)
                        .map(Operations::filter)
                        .map(Operations::count)
                        .subscribeOn(Schedulers.single())
                //perfetto, così facendo non
                // faccio fare niente alla gui che rimane reattiva
        ).subscribe(v -> {
            Operations.log("creating the map and updating the view");
            v.forEach((s, c)-> {
                map.merge(s, c, Integer::sum);
            });
            SwingUtilities.invokeLater(()->{
                gui.update(map);
            });
        });

    }
}