package com.github.scarecrow.signscognizing.Utilities;

import android.icu.util.ICUUncheckedIOException;
import android.util.Log;

import com.github.scarecrow.signscognizing.fragments.InputControlPanelFragment;

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

    private boolean capture_state = false;
    // false -> 没有采集
    // true  -> 采集中


    public static MessageManager getInstance() {
        return instance;
    }

    private int acquire_curr_id() {
        return messages_list.size();
    }

    /**
     * 这个方法被用于inputControl的fragment中
     * 在这个fragment中点按创建的手语识别消息都是新建的
     * 该方法先新建一个手语消息实例并并显示出来 并将该实例暂存下来
     * 当时识别完成后使用回调更新该手语的数据
     */
    private SignMessage new_added_msg;

    public void buildSignMessage() {
        Armband armband = ArmbandManager.getArmbandsManger()
                .getCurrentConnectedArmband();
        new_added_msg = new SignMessage("正在识别手语中", 0, armband);
        messages_list.add(new_added_msg);
        noticeAllTargetMsgAdded();
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


    /**
     * 当返回一条手语识别的消息调用后 更新一个手语消息的实例
     * 有两种情况 一种是新创建的手语消息实例 另一种是重发的手语
     * 通过手语的id进行map判断这个手语是否被识别过一次
     *
     * @param text          手语的文字内容 来自服务器
     * @param sign_id       手语的id码 来自服务器给定
     * @return 新生成的手语消息对象
     */
    public void updateSignMessage(String text, int sign_id, int capture_id) {
        SignMessage new_msg;
        if (sign_message_map.containsKey(sign_id)) {
            new_msg = sign_message_map.get(sign_id);
            new_msg.setTextContent(text);
        } else {
            new_added_msg.setCaptureId(capture_id);
            new_added_msg.setTextContent(text);
            new_added_msg.setMsgId(sign_id);
            sign_message_map.put(sign_id, new_added_msg);
        }
        noticeAllTargetMsgChange();
    }

    public void updateSignMessage(String feedback_json) {
        try {
            JSONObject jsonObject = new JSONObject(feedback_json);
            String control_info = jsonObject.getString("control");
            if (control_info.equals("update_recognize_res")) {
                updateSignMessage(jsonObject.getString("text"),
                        jsonObject.getInt("sign_id"),
                        jsonObject.getInt("capture_id"));

            } else if (control_info.equals("end_recognize")) {
                int sign_id = jsonObject.getInt("sign_id");
                if (sign_message_map.containsKey(sign_id)) {
                    sign_message_map.get(sign_id).setCaptureComplete(true);
                    noticeAllTargetSignCaptureEnd();
                    noticeAllTargetMsgChange();
                }
            }
        } catch (Exception ee) {
            Log.e(TAG, "buildSignMessage:  error: " + ee);
            ee.printStackTrace();
        }
    }

    public boolean requestCaptureSign() {
        if (capture_state) {
            Log.e(TAG, "requestCaptureSign: sign capturing repeat");
            return false;
        }
        capture_state = true;
        SocketConnectionManager.getInstance()
                .sendMessage(buildSignRecognizeRequest(0));
        MessageManager.getInstance()
                .buildSignMessage();
        return true;
    }

    public boolean recaptureSignRequest(SignMessage message) {
        if (capture_state) {
            Log.e(TAG, "requestCaptureSign: sign capturing repeat");
            return false;
        }
        capture_state = true;
        message.setCaptureComplete(false);
        noticeAllTargetMsgSignCaptureStart();
        SocketConnectionManager.getInstance()
                .sendMessage(buildSignRecognizeRequest(message.getMsgId()));
        return true;
    }

    /**
     * 手语识别请求体构造
     * 如果是新增识别， 的 sign_id字段使用0 标识
     * 如："data": {"sign_id" :0}
     *
     * @return 请求的json
     */
    public String buildSignRecognizeRequest(int sign_id) {
        String armband_id = ArmbandManager.getArmbandsManger()
                .getCurrentConnectedArmband()
                .getArmband_id();
        JSONObject request_body = new JSONObject();
        try {
            request_body.accumulate("control", "sign_cognize_request");
            JSONObject data = new JSONObject();
            data.accumulate("armband_id", armband_id);
            data.accumulate("sign_id", sign_id);
            request_body.accumulate("data", data);
        } catch (Exception ee) {
            Log.e(TAG, "buildSignRecognizeRequest: on build request json " + ee);
            ee.printStackTrace();
        }
        return request_body.toString();
    }


    public boolean isCapturingSign() {
        return capture_state;
    }

    public List<ConversationMessage> getMessagesList() {
        return messages_list;
    }

    public void addNewNoticeTarget(NoticeMessageChanged obj) {
        notice_list.add(obj);
    }

    private void noticeAllTargetSignCaptureEnd() {
        capture_state = false;
        for (NoticeMessageChanged obj : notice_list) {
            obj.onSignCaptureEnd();
        }
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

    private void noticeAllTargetMsgSignCaptureStart() {
        for (NoticeMessageChanged obj : notice_list) {
            obj.onSignCaptureStart();
        }
    }

    public interface NoticeMessageChanged {
        void onNewMessageAdd();
        void onMessageContentChange();

        void onSignCaptureStart();

        void onSignCaptureEnd();
    }
}
