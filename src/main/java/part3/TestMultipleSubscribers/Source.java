package part3.TestMultipleSubscribers;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.io.File;


public class Source extends Thread {

    Flowable<Integer> source = Flowable.range(0,10);

    public Flowable<Integer> getObservable(){
        return source.map(integer -> integer*integer);
    }


}
