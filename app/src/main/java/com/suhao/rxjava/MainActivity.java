package com.suhao.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //被观察者
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            /**
             * ObservableEmitter-->发射器，用来发出事件
             * 一共三种类型的事件：
             * onNext-->next事件
             * onComplete-->complete事件
             * onError-->error事件
             * 1.上游可以发送无限个onNext事件，下游也可以接收无限个onNext事件
             * 2.上游发送onComplete事件以后，可以继续发送onNext。但是下游接收到onComplete后不再接收后续事件
             * 3.上游发送onError事件以后，可以继续发送onNext。但是下游接收到onError后不再接收后续事件
             * 4.上游可以不发送onComplete或onError
             * 5.onComplete和onError必须唯一并且互斥，即不能发多个onComplete, 也不能发多个onError, 也不能先发一个onComplete, 然后再发一个onError, 反之亦然
             */
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });
        //观察者
        Observer<Integer> observer = new Observer<Integer>() {
            /**
             * Disposable,可以理解成控制上游和下游的开关
             * 调用它的dispose()，会切换上下游，导致下游收不到事件，但是不会导致上游不再继续发送事件
             * @param d
             */
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, integer + "");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };
        //建立连接
        observable.subscribe(observer);
        callChaining();
        dispose();
    }

    /**
     * 链式调用
     */
    private void callChaining() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onNext(6);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, integer + "");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        });
    }

    private void dispose(){
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG,"emit 1");
                emitter.onNext(1);
                Log.i(TAG,"emit 2");
                emitter.onNext(2);
                Log.i(TAG,"emit 3");
                emitter.onNext(3);
                Log.i(TAG,"emit complete");
                emitter.onComplete();
                Log.i(TAG,"emit 4");
                emitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {

            private Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG,"onSubscribe");
                disposable=d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG,integer+"");
                if(integer==2){
                    disposable.dispose();
                    Log.i(TAG,"isDisposed:"+disposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG,"onComplete");
            }
        });
    }
}
