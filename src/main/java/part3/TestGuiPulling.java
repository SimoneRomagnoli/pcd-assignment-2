package part3;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import part3.model.FlowableOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TestGuiPulling extends JFrame {

    Map<String, Integer> map = new HashMap<>();

    public static void main(String[] args) {

        File directory = new File("/home/mr/Documents/Magistrale/pcd/Assignments/pcd-assignment-2/resLong");
        List<File> documents = Arrays.asList(Objects.requireNonNull(directory.listFiles()));

        System.out.println("File extracted form the directory, number of files = " + documents.size());

        Flowable<File> source = Flowable.fromIterable(documents);

        SwingUtilities.invokeLater(() -> {
            new TestGuiPulling(source);
        });

    }

    public TestGuiPulling(Flowable<File> stream){
        super("Swing + RxJava");
        setSize(200,200);
        setVisible(true);
        JTextArea text = new JTextArea();
        JButton bt = new JButton("unsubscribe");
        JButton bt1 = new JButton("subscribe");


        Disposable disp  = stream.flatMap(s -> Flowable.just(s)
                        .subscribeOn(Schedulers.computation())
                        .map(FlowableOperations::loadAndGetChuncks)
                        .flatMap(Flowable::fromIterable)
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

            map.keySet()
                .stream()
                .sorted((a, b) -> map.get(b) - map.get(a))
                .limit(5)
                .collect(Collectors.toMap(k -> k, map::get))
                .forEach((k,c)-> text.append(k + " " + c + "\n"));
        });

        bt.addActionListener(l->{
            System.out.println("unsubscribing");
            disp.dispose();
        });


        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(text,BorderLayout.NORTH);
        getContentPane().add(bt, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(-1);
            }
        });
    }
}
