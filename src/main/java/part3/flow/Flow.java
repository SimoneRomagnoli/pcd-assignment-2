package part3.flow;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;
import java.util.*;

/**
 * Class representing the model of the program:
 * here is nested the entire logic of the program.
 */
public class Flow {

    private final List<File> documents;


    public Flow(File dir) {
        this.documents = new ArrayList<>();
        this.documents.addAll(Arrays.asList(Objects.requireNonNull(dir.listFiles())));
    }


    public  Flowable<Map<String,Integer>>  getMapsFlowable() {
        Flowable<File> source = Flowable.fromIterable(documents);

        return source.flatMap(s->Flowable.just(s)
                .subscribeOn(Schedulers.computation())
                .map(FlowableOperations::loadAndGetChuncks)
                .flatMap(Flowable::fromIterable)
                .map(FlowableOperations::split)
                .map(FlowableOperations::filter)
                .map(FlowableOperations::count));
    }
}
