package com.example.cmc_application.comm;

import android.os.HandlerThread;
import android.serialport.SerialPort;

import com.example.cmc_application.util.ByteUtil;
import com.licheedev.myutils.LogPlus;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SerialPortManager {

    private static final String TAG = "SerialPortManager";

    private SerialReadThread mReadThread;
    private SerialSendThread mSendThread;

    private static class InstanceHolder {

        public static SerialPortManager sManager = new SerialPortManager();
    }

    public static SerialPortManager instance() {
        return InstanceHolder.sManager;
    }

    private SerialPort mSerialPort;

    private SerialPortManager() {
    }

    public SerialPort init(Device device) {
        return init(device.getPath(), device.getBaudrate());
    }

    public SerialPort init(String devicePath, String baudrateString) {
        if (mSerialPort != null) {
            close();
        }

        try {
            File device = new File(devicePath);
            int baurate = Integer.parseInt(baudrateString);
            mSerialPort = new SerialPort(device, baurate, 0);

            mReadThread = new SerialReadThread(mSerialPort.getInputStream());
            mSendThread = new SerialSendThread(mSerialPort.getOutputStream());

            return mSerialPort;
        } catch (Throwable tr) {
            LogPlus.e(TAG, "打開串口失敗", tr);
            close();
            return null;
        }
    }

    public void mReadOpen() {
        if (mReadThread.getState() == Thread.State.NEW)
            mReadThread.start();
        else if (mReadThread.getState() == Thread.State.TERMINATED) {
            try {
                mReadThread = new SerialReadThread(mSerialPort.getInputStream());
                mReadThread.start();
            } catch (Exception e) {

            }
        }
    }

    public void mReadClose(){
        if (mReadThread != null)
            mReadThread.close();
    }

    public void mSendOpen() {
        if (mSendThread.getState() == Thread.State.NEW)
            mSendThread.start();
        else if (mSendThread.getState() == Thread.State.TERMINATED) {
            try {
                mSendThread = new SerialSendThread(mSerialPort.getOutputStream());
                mSendThread.start();
            } catch (Exception e) {

            }
        }
    }

    public void mSendClose(){
        if (mSendThread != null)
            mSendThread.close();
    }

    public void sendCommand(String command){
        mSendThread.sendCommand(command);
    }



    public void close() {
//        if (mReadThread != null) {
//            mReadThread.close();
//        }
//        if (mOutputStream != null) {
//            try {
//                mOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }


}
