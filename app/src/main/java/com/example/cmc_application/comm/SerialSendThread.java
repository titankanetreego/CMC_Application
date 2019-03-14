package com.example.cmc_application.comm;

import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

import com.example.cmc_application.util.ByteUtil;
import com.licheedev.myutils.LogPlus;

import org.greenrobot.eventbus.EventBus;

import java.io.OutputStream;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SerialSendThread extends Thread{
    private static final String TAG = "SerialSendThread";

    private OutputStream mOutputStream;
    private HandlerThread mWriteThread;
    private Scheduler mSendScheduler;

    private static String session_1 = "200300320008";
    private static String session_2 = "20030040000A";
    private static String session_3 = "2003007C0003";

    private static final int sleepTime = 100;

    public SerialSendThread(OutputStream mOutputStream){
        this.mOutputStream = mOutputStream;
        mWriteThread = new HandlerThread("write-thread");
        mWriteThread.start();
        mSendScheduler = AndroidSchedulers.from(mWriteThread.getLooper());

        session_1 = session_1 + ByteUtil.getCrcCheckStr(session_1);
        session_2 = session_2 + ByteUtil.getCrcCheckStr(session_2);
        session_3 = session_3 + ByteUtil.getCrcCheckStr(session_3);

    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            sendCommand(session_1);
            SystemClock.sleep(sleepTime);
            sendCommand(session_2);
            SystemClock.sleep(sleepTime);
            sendCommand(session_3);
            SystemClock.sleep(sleepTime);
        }
    }

    public void close() {
        this.interrupt();
    }

    private void sendData(byte[] datas) throws Exception {
        mOutputStream.write(datas);
    }

    private Observable<Object> rxSendData(final byte[] datas) {

        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                try {
                    sendData(datas);
                    emitter.onNext(new Object());
                } catch (Exception e) {

                    LogPlus.e("發送：" + ByteUtil.bytes2HexStr(datas) + " 失敗", e);

                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                        return;
                    }
                }
                emitter.onComplete();
            }
        });
    }

    public void sendCommand(final String command) {

        byte[] bytes = ByteUtil.hexStr2bytes(command);
        rxSendData(bytes).subscribeOn(mSendScheduler).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                EventBus.getDefault().post(new SendMessage(command));
            }

            @Override
            public void onError(Throwable e) {
                LogPlus.e("發送失敗", e);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
