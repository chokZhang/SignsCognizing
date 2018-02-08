package com.github.scarecrow.signscognizing.Utilities;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * Created by Scarecrow on 2018/2/8.
 */

public class SocketCommunicatorThread extends HandlerThread {

    private static String TAG = "SocketCommunicator";

    private Handler main_thread_handler,
            communicator_handler;

    public SocketCommunicatorThread(Handler main_thread_handler) {
        super(TAG);
        this.main_thread_handler = main_thread_handler;
    }

    @Override
    protected void onLooperPrepared() {
        communicator_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SocketConnectionManager.TASK_BUILD_UP_CONNECTION:
                        try {
                            //这里进行套接字的连接建立工作
                            sleep(1000);
                        } catch (Exception ee) {
                            Log.e(TAG, "handleMessage: while sleeping", ee);
                        }
                        main_thread_handler.obtainMessage(SocketConnectionManager.CONNECT_SUCCESS)
                                .sendToTarget();
                        break;
                }
            }
        };

        main_thread_handler.obtainMessage(SocketConnectionManager.LOOPER_READY)
                .sendToTarget();
    }

    public void startConnection() {
        communicator_handler.obtainMessage(SocketConnectionManager.TASK_BUILD_UP_CONNECTION)
                .sendToTarget();
    }

}
