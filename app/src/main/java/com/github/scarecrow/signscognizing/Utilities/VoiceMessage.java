package com.github.scarecrow.signscognizing.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/8.
 * 一开始语音消息是没有文字的  需要在后续调用转换
 */

public class VoiceMessage extends ConversationMessage {

    private static EventManager asrEventManager;
    private String voice_file_path;
    //  用于存放识别结果
    private String result_buffer = "";
    //  接受语音识别请求的Event
    private EventListener asr_result_listener;
    //    接受回调的listener
    @SuppressLint("HandlerLeak")
    private Handler finish_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            text_content = (String) msg.obj;
            MessageManager.getInstance()
                    .noticeAllTargetMsgChange();
            asrEventManager.unregisterListener(asr_result_listener);
        }
    };

    /**
     * 语音消息录音完毕后 会将语音文件的URL传入
     * 随后根据这个URL开始语音转文字
     * @param msg_id message id
     * @param voice_file_path  语音文件路径
     */
    public VoiceMessage(int msg_id, String voice_file_path) {
        super(msg_id, ConversationMessage.VOICE, "正在识别语音");
        this.voice_file_path = voice_file_path;
        Log.d(TAG, "VoiceMessage: building new msg voice path: " + voice_file_path);
        transVoice2Text();
    }

    /**
     * 初始化语音识别的引擎
     *
     * @param context
     */
    static public void initASR(Context context) {
        asrEventManager = EventManagerFactory.create(context, "asr");
        Map<String, Object> map = new HashMap<String, Object>();
//        设置为混合引擎 并加载本地语音识别模型
        map.put(SpeechConstant.DECODER, 2);
        map.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets:///baidu_speech_grammar.bsg");
        JSONObject json = new JSONObject(map);
        asrEventManager.send(SpeechConstant.ASR_KWS_LOAD_ENGINE, json.toString(), null, 0, 0);

    }

    static public void releaseASR() {
        asrEventManager.send(SpeechConstant.ASR_KWS_UNLOAD_ENGINE, "", null, 0, 0);
        asrEventManager.send(SpeechConstant.ASR_STOP, "", null, 0, 0);
    }

    private void transVoice2Text() {
        //16bit，16000hz，单声道的pcm,wav文件
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    asr_result_listener = new EventListener() {
                        @Override
                        public void onEvent(String name, String params, byte[] data, int offset, int length) {
                            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                                try {
                                    Log.d(TAG, "onEvent: CALLBACK_EVENT_ASR_PARTIAL: " + params);
                                    JSONObject result = new JSONObject(params);
                                    result_buffer = result.getJSONArray("results_recognition")
                                            .getString(0);

                                } catch (Exception ee) {
                                    Log.e(TAG, "onEvent: CALLBACK_EVENT_ASR_PARTIAL");
                                    ee.printStackTrace();
                                }
                            }
                            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                                Log.d(TAG, "onEvent: ASR finish result param :" + params);
                                finish_handler.obtainMessage(0, result_buffer)
                                        .sendToTarget();

                            }
                            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)) {
                                Log.d(TAG, "onEvent: ASR CALLBACK_EVENT_ASR_EXIT");
                            }

                            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_ERROR)) {
                                Log.e(TAG, "onEvent: 语音识别引擎出错 " + params);
                            }

                            // ... 支持的输出事件和事件支持的事件参数见“输入和输出参数”一节
                        }
                    };
                    asrEventManager.registerListener(asr_result_listener);
                    System.gc(); // 不加GC会导致SDK内部报数组越界？？？？
                    asrEventManager.send(SpeechConstant.ASR_START, buildParams(), null, 0, 0);
                }
            }).start();

        } catch (Exception ee) {
            Log.e(TAG, "transVoice2Text: " + ee);
            ee.printStackTrace();
        }
    }

    private String buildParams() {
        String res = "";
        try {
            JSONObject param = new JSONObject();
            param.accumulate(SpeechConstant.APP_NAME, "com.github.scarecrow.signscognizing");
            param.accumulate(SpeechConstant.APP_ID, "11138165");
            param.accumulate(SpeechConstant.APP_KEY, "atDLVSr4NFmDNPxPWHxWnPVS");
            param.accumulate(SpeechConstant.SECRET, "20da52346b042869be7cda3f8fb12cf5");
            param.accumulate(SpeechConstant.PID, "1536");
            param.accumulate(SpeechConstant.DISABLE_PUNCTUATION, false);
            param.accumulate(SpeechConstant.DECODER, 2);
            param.accumulate(SpeechConstant.IN_FILE, voice_file_path);
            param.accumulate(SpeechConstant.NLU, "enable");
            res = param.toString();
        } catch (Exception ee) {
            Log.e(TAG, "buildParams: error" + ee);
            ee.printStackTrace();
        }
        return res;
    }

    /**
     * 科大讯飞语音包使用的各种模拟音频输入识别的方法 已废弃
     * 如果直接从音频文件识别，需要模拟真实的音速，防止音频队列的堵塞
     * @throws InterruptedException sleep 方法
     */
    private void recognizePcmfileByte() throws InterruptedException {
        // 1、读取音频文件
        FileInputStream fis = null;
        byte[] voice_buffer;
        try {
            fis = new FileInputStream(new File(voice_file_path));
            voice_buffer = new byte[fis.available()];
            fis.read(voice_buffer);
        } catch (Exception e) {
            Log.e(TAG, "recognizePcmfileByte: failed to open voice file ", e);
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 2、音频流听写
        if (0 == voice_buffer.length) {
            result_buffer = "no audio avaible!";
        }
    }

    /**
     * 将字节缓冲区按照固定大小进行分割成数组
     *
     * @param buffer 缓冲区
     * @param length 缓冲区大小
     * @param spsize 切割块大小
     * @return
     */
    private ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
        ArrayList<byte[]> array = new ArrayList<>();
        if (spsize <= 0 || length <= 0 || buffer == null
                || buffer.length < length)
            return array;
        int size = 0;
        while (size < length) {
            int left = length - size;
            if (spsize < left) {
                byte[] sdata = new byte[spsize];
                System.arraycopy(buffer, size, sdata, 0, spsize);
                array.add(sdata);
                size += spsize;
            } else {
                byte[] sdata = new byte[left];
                System.arraycopy(buffer, size, sdata, 0, left);
                array.add(sdata);
                size += left;
            }
        }
        return array;
    }



}
