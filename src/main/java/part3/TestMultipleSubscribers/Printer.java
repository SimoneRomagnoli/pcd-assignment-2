package part3.TestMultipleSubscribers;

import io.reactivex.rxjava3.core.Flowable;

public class Printer{
    private final Flowable<Integer> flowable;
    private final Source source;

    public Printer(Source src) {
        this.source = src;
        this.flowable =src.getObservable();
    }

    public void register(){
        flowable.subscribe(System.out::println);
    }


}
