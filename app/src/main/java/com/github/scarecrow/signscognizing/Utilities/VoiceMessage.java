package com.github.scarecrow.signscognizing.Utilities;

import android.annotation.SuppressLint;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/8.
 * 一开始语音消息是没有文字的  需要在后续调用转换
 */

public class VoiceMessage extends ConversationMessage {

    private String voice_file_path;


    public VoiceMessage(int msg_id, String voice_file_path) {
        super(msg_id, ConversationMessage.VOICE, "正在识别语音");
        this.voice_file_path = voice_file_path;
        transVoice2Text();
    }


    private void transVoice2Text() {
        // 将这个函数放在asynctask里面用
        // 返回String后修改UI即可
        //在这里调用科大讯飞的api语音转文字

        //todo 16bit，16000hz，单声道的pcm,wav文件
        try {
            SpeechRecognizer speechRecognizer = SpeechRecognizer.getRecognizer();
            speechRecognizer.setParameter(SpeechConstant.APPID, "5a883f0c");
            speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "ch_zn");
            speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
            speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
            speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, voice_file_path);
            speechRecognizer.startListening(new RecognizerListener() {
                @Override
                public void onVolumeChanged(int i, byte[] bytes) {

                }

                @Override
                public void onBeginOfSpeech() {
                    Log.d(TAG, "onBeginOfSpeech: ");
                }

                @Override
                public void onEndOfSpeech() {
                    Log.d(TAG, "onEndOfSpeech: ");
                }

                @Override
                public void onResult(RecognizerResult recognizerResult, boolean b) {
                    setTextContent(recognizerResult.getResultString());
                }

                @Override
                public void onError(SpeechError speechError) {

                }

                @Override
                public void onEvent(int i, int i1, int i2, Bundle bundle) {

                }
            });
        } catch (Exception ee) {
            Log.e(TAG, "transVoice2Text: " + ee);
            ee.printStackTrace();
        }

    }

    private Runnable trans_task = new Runnable() {
        @Override
        public void run() {
            try {
                SpeechRecognizer speechRecognizer = SpeechRecognizer.getRecognizer();
                speechRecognizer.setParameter(SpeechConstant.APPID, "5a883f0c");
                speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
                speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
                speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "ch_zn");
                speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
                speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                speechRecognizer.startListening(new RecognizerListener() {
                    @Override
                    public void onVolumeChanged(int i, byte[] bytes) {

                    }

                    @Override
                    public void onBeginOfSpeech() {
                        Log.d(TAG, "onBeginOfSpeech: ");
                    }

                    @Override
                    public void onEndOfSpeech() {
                        Log.d(TAG, "onEndOfSpeech: ");
                    }

                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        finish_handler.obtainMessage(0, recognizerResult.getResultString())
                                .sendToTarget();
                    }

                    @Override
                    public void onError(SpeechError speechError) {

                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {

                    }
                });
            } catch (Exception ee) {
                Log.e(TAG, "transVoice2Text: " + ee);
                ee.printStackTrace();
            }

        }
    };

    @SuppressLint("HandlerLeak")
    private Handler finish_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            text_content = (String) msg.obj;
            MessageManager.getInstance()
                    .noticeAllTargetMsgChange();
        }
    };


}
