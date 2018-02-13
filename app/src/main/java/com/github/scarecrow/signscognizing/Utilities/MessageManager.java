package com.github.scarecrow.signscognizing.Utilities;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/8.
 * 消息id分为两种 一种是本地的id 给予文字和语音识别消息的id
 * 这些消息不需要和服务器进行交互 仅在本地进行管理即可。
 * 还有一种是手语消息的id 由于手语可能需要进行重发
 * 所以需要与服务端进行进行同步工作 手语数据主要是在服务端进行管理
 * 为了便于服务端手语数据的管理 这个id从服务端获取。
 */

public class MessageManager {
    private MessageManager() {
        messages_list = new ArrayList<>();
    }

    private static MessageManager instance = new MessageManager();

    private List<ConversationMessage> messages_list;

    private List<NoticeMessageChanged> notice_list = new ArrayList<>();

    private Map<Integer, SignMessage> sign_message_map = new Hashtable<>();

    private int sign_id;

    public static MessageManager getInstance() {
        return instance;
    }

    private int acquire_curr_id() {
        return messages_list.size();
    }


    /**
     * 构造或更新一个手语消息的实例
     *
     * @param text          手语的文字内容 来自服务器
     * @param sign_id       手语的id码 来自服务器给定
     * @return 新生成的手语消息对象
     */
    public SignMessage updateSignMessage(String text, int sign_id) {
        SignMessage new_msg;
        if (sign_message_map.containsKey(sign_id)) {
            new_msg = sign_message_map.get(sign_id);
            new_msg.setTextContent(text);
            noticeAllTargetMsgChange();
        } else {
            new_msg = new SignMessage(text, sign_id,
                    ArmbandManager.getArmbandsManger()
                            .getCurrentConnectedArmband());
            messages_list.add(new_msg);
            sign_message_map.put(sign_id, new_msg);
            noticeAllTargetMsgAdded();
        }

        return new_msg;
    }

    public SignMessage updateSignMessage(String feedback_json) {
        SignMessage signMessage = null;
        try {
            JSONObject jsonObject = new JSONObject(feedback_json);
            signMessage = updateSignMessage(jsonObject.getString("text"),
                    jsonObject.getInt("sign_id"));
        } catch (Exception ee) {
            Log.e(TAG, "buildSignMessage:  error: " + ee);
            ee.printStackTrace();
        }
        return signMessage;
    }

    public TextMessage buildTextMessage(String text) {
        TextMessage new_msg = new TextMessage(acquire_curr_id(), text);
        messages_list.add(new_msg);
        noticeAllTargetMsgAdded();
        return new_msg;
    }

    public VoiceMessage buildVoiceMessage(String voice_path) {
        VoiceMessage new_msg = new VoiceMessage(acquire_curr_id(), voice_path);
        messages_list.add(new_msg);
        noticeAllTargetMsgAdded();
        return new_msg;
    }

    public void syncSignId() {
        //todo sync sign id

    }

    public List<ConversationMessage> getMessagesList() {
        return messages_list;
    }

    public void addNewNoticeTarget(NoticeMessageChanged obj) {
        notice_list.add(obj);
    }

    private void noticeAllTargetMsgAdded() {
        for (NoticeMessageChanged obj : notice_list) {
            obj.onNewMessageAdd();
        }
    }

    public void noticeAllTargetMsgChange() {
        for (NoticeMessageChanged obj : notice_list) {
            obj.onMessageContentChange();
        }
    }

    public interface NoticeMessageChanged {
        void onNewMessageAdd();

        void onMessageContentChange();
    }
}
