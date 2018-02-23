package com.github.scarecrow.signscognizing.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/8.
 * 套接字连接的接口类 单例
 * 这个类直接构造和管理套接字连接的线程类(包括一个链接线程 一个监听线程)
 * 外部通过该单例来使用套接字线程 使用时需要传入一个回调类
 * 当连接收到消息或者状态发生变化时 通过回调传递消息
 */

public class SocketConnectionManager {

    //task_code
    public static final int TASK_BUILD_UP_CONNECTION = 9,
            TASK_SEND_INFO = 632;

    // result_code
    // 来自连接线程发回的消息 表示连接线程的任务完成情况
    // 从而进行下一步的工作
    public static final int LOOPER_READY = 57,
            CONNECT_SUCCESS = 56,
            CONNECT_FAILED = 514,
            DISCONNECT = 46,
            RECEIVE_MESSAGE = 75;


    //status code
    public static final int DISCONNECTED = 979,
            CONNECTED = 56;

    private int manager_status = DISCONNECTED;

    private static SocketConnectionManager instance = new SocketConnectionManager();
    private SocketCommunicatorThread socket_communicator;
    private List<TaskCompleteCallback> listener_list;

    // 负责从连接线程接受消息然后通过回调与外界进行互动的handler
    // 该处在主线程
    @SuppressLint("HandlerLeak")
    private Handler main_thread_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT_FAILED:
                    ArmbandManager.getArmbandsManger()
                            .setCurrentConnectedArmband(null);
                    manager_status = DISCONNECTED;
                    noticeListeners(CONNECT_FAILED, null);
                    break;

                case CONNECT_SUCCESS:
                    manager_status = CONNECTED;
                    noticeListeners(CONNECT_SUCCESS, null);
                    break;

                case RECEIVE_MESSAGE:
                    String info = (String) msg.obj;
                    Log.d(TAG, "handleMessage: receive message : " + info);
                    noticeListeners(RECEIVE_MESSAGE, info);
                    break;

                case LOOPER_READY:
                    socket_communicator.startConnection();
                    break;

                case DISCONNECT:
                    disconnect();
                    break;

                default:
                    Log.e(TAG, "SocketConnectionManager handleMessage: unknown message " +
                            "message_id : " + msg.what);
                    break;
            }

        }
    };

    public static SocketConnectionManager getInstance() {
        return instance;
    }

    public void startConnection(Armband target_armband,
                                TaskCompleteCallback callbackListener) {
        // 这里调连接线程 进行socket连接
        Log.d(TAG, "startConnection: 连接手环： " + target_armband + "中。。");
        socket_communicator = new SocketCommunicatorThread(main_thread_handler);
        socket_communicator.start();
        socket_communicator.getLooper();
        listener_list = new ArrayList<>();
        listener_list.add(callbackListener);
        ArmbandManager.getArmbandsManger()
                .setCurrentConnectedArmband(target_armband);
        //当communicator的looper准备完毕后 会通过handler发消息的方式进行连接请求
    }

    public void sendMessage(String message) {
        socket_communicator.sendMessage(message);
    }

    public void disconnect() {
        manager_status = DISCONNECTED;
        ArmbandManager.getArmbandsManger()
                .setCurrentConnectedArmband(null);
        noticeListeners(DISCONNECT, null);
        listener_list.clear();
        socket_communicator.disconnect();

    }

    public void addTaskCallback(TaskCompleteCallback callback) {
        listener_list.add(callback);
    }


    private void noticeListeners(int type, String data) {
        switch (type) {
            case CONNECT_SUCCESS:
                for (TaskCompleteCallback callback : listener_list)
                    callback.onConnectSucceeded();
                break;
            case CONNECT_FAILED:
                for (TaskCompleteCallback callback : listener_list)
                    callback.onConnectFailed();
                break;
            case RECEIVE_MESSAGE:
                for (TaskCompleteCallback callback : listener_list)
                    callback.onReceivedMessage(data);
                break;
            case DISCONNECT:
                for (TaskCompleteCallback callback : listener_list)
                    callback.onDisconnect();

            default:
                break;

        }
    }


    public interface TaskCompleteCallback {
        void onConnectSucceeded();

        void onConnectFailed();

        void onReceivedMessage(String message);

        void onDisconnect();
    }
}
