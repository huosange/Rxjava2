package com.suhao.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyActivity02 extends AppCompatActivity {

    public static final String TAG="MyActivity02";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Observable<Integer> observable=Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG,"observable thread is:"+Thread.currentThread().getName());
                Log.i(TAG,"emit 1");
                emitter.onNext(1);
            }
        });

        Consumer<Integer> consumer=new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG,"observer thread is:"+Thread.currentThread().getName());
                Log.i(TAG,"onNext:"+integer);
            }
        };
        //observable.subscribe(consumer);
        //线程控制
        /**
         * subscribeOn()指定上游发送事件的线程
         * observeOn()指定下游接收事件的线程
         * 多次指定上游线程只有第一次指定有效
         * 多次指定下游线程是可以的，每调用一次observeOn()，下游的线程就会切换一次
         */
//        observable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(consumer);
//        observable.subscribeOn(Schedulers.newThread())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .observeOn(Schedulers.io())
//                .subscribe(consumer);

        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG,"after observeOn(mainThread),current thread is:"+Thread.currentThread().getName());
                    }
                }).observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG,"after observeOn(io),current thread is:"+Thread.currentThread().getName());
                    }
                })
                .subscribe(consumer);
    }
}
