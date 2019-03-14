package com.example.cmc_application.comm;

import android.os.SystemClock;

import com.example.cmc_application.util.ByteUtil;
import com.licheedev.myutils.LogPlus;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SerialReadThread extends Thread {

    private static final String TAG = "SerialReadThread";

    private BufferedInputStream mInputStream;

    public SerialReadThread(InputStream is) {
        mInputStream = new BufferedInputStream(is);
    }

    @Override
    public void run() {
        LogPlus.e("開始讀取線呈");

        while (!this.isInterrupted()) {
            byte[] received = new byte[1024];
            int size;

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {
                int available = mInputStream.available();

                if (available > 0) {
                    size = mInputStream.read(received);
                    if (size > 0) {
                        onDataReceive(received, size);
                    }
                }
                else {
                    SystemClock.sleep(100);
                }
            } catch (IOException e) {
                LogPlus.e("讀取數據失敗", e);
            }
            //Thread.yield();
        }

        LogPlus.e("結束讀取線呈");
    }


    private void onDataReceive(byte[] received, int size) {
        String hexStr = ByteUtil.bytes2HexStr(received, 0, size);
        EventBus.getDefault().post(new ReceivedMessage(hexStr));
    }


    public void close() {
        this.interrupt();
    }
}
