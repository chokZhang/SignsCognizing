package com.github.scarecrow.signscognizing.Utilities;

/**
 * Created by Scarecrow on 2018/2/8.
 */

public abstract class ConversationMessage {
    public static final int SIGN = 714,
            VOICE = 564,
            TEXT = 654;

    protected int msg_id;
    protected String text_content;
    protected int msg_type;

    protected ConversationMessage(int msg_id, int msg_type, String text_content) {
        this.msg_id = msg_id;
        this.msg_type = msg_type;
        this.text_content = text_content;
    }

    public int getMsgId() {
        return msg_id;
    }

    public int getMsgType() {
        return msg_type;
    }

    public String getTextContent() {
        return text_content;
    }

    protected void setTextContent(String content) {
        text_content = content;
    }
}
