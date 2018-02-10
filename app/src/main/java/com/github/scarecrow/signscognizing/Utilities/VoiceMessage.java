package com.github.scarecrow.signscognizing.Utilities;

import android.os.AsyncTask;
import android.widget.EditText;

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
        new TransVoice().execute(this);

        //在这里调用科大讯飞的api语音转文字
    }

    private static class TransVoice extends AsyncTask<VoiceMessage, Void, Void> {
        @Override
        protected Void doInBackground(VoiceMessage... params) {
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
            params[0].setTextContent("语音识别结果");

            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            MessageManager.getInstance().noticeAllTargetMsgChange();
        }

    }
}
