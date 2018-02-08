package com.github.scarecrow.signscognizing.Utilities;

import android.os.Message;

import android.os.Handler;
import android.util.Log;

import java.util.logging.LogRecord;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/8.
 */

public class SocketConnectionManager {

    //task_code
    public static final int TASK_BUILD_UP_CONNECTION = 9;

    //result_code
    public static final int LOOPER_READY = 57,
            CONNECT_SUCCESS = 56,
            CONNECT_FALIED = 514;

    //status code
    public static final int DISCONNECTED = 979,
            CONNECTED = 56;

    private SocketConnectionManager() {
    }

    private static SocketConnectionManager instance = new SocketConnectionManager();

    private SocketCommunicatorThread socket_communicator;

    private int manager_status = DISCONNECTED;

    private Handler main_thread_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_FALIED:
                    ArmbandManager.getArmbandsManger()
                            .setCurrentConnectedArmband(null);

                    taskCallbackListenner.onConnectFailed();
                    break;
                case CONNECT_SUCCESS:
                    taskCallbackListenner.onConnectSuccess();
                    break;
                case LOOPER_READY:
                    socket_communicator.startConnection();
                default:
                    Log.e(TAG, "SocketConnectionManager handleMessage: unknown message");
                    break;
            }

        }
    };

    private TaskCallbackListenner taskCallbackListenner;


    public static SocketConnectionManager getInstance() {
        return instance;
    }

    public void startConnection(Armband target_armband,
                                TaskCallbackListenner callbackListenner) {
        // 这里调连接线程 进行socket连接
        Log.d(TAG, "startConnection: 连接手环： " + target_armband + "中。。");
        socket_communicator = new SocketCommunicatorThread(main_thread_handler);
        socket_communicator.start();
        socket_communicator.getLooper();
        taskCallbackListenner = callbackListenner;
        ArmbandManager.getArmbandsManger()
                .setCurrentConnectedArmband(target_armband);
        //当communicator的looper准备完毕后 会通过handler发消息的方式进行连接请求

    }

    public void disconnected() {
        manager_status = DISCONNECTED;
        ArmbandManager.getArmbandsManger()
                .setCurrentConnectedArmband(null);

    }


    public interface TaskCallbackListenner {
        public void onConnectSuccess();

        public void onConnectFailed();
    }
}
