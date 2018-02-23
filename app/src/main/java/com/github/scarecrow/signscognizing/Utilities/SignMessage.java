package com.github.scarecrow.signscognizing.Utilities;

/**
 * Created by Scarecrow on 2018/2/6.
 *
 */

public class SignMessage extends ConversationMessage {
    public static final int CONFIRMED_CORRECT = 564,
            CONFIRMED_WRONG = 456,
            NO_RECAPTURE = 789,
            INITIAL = 2541;

    private int sign_feedback_stauts;

    private int capture_id = 0;

    private boolean is_capture_complete = false;

    private Armband capture_armband;

    public SignMessage(String text, int msg_id, Armband capture_armband) {
        super(msg_id, ConversationMessage.SIGN, text);
        this.capture_armband = capture_armband;
        sign_feedback_stauts = INITIAL;
    }

    public void setSignFeedbackStatus(int stauts) {
        sign_feedback_stauts = stauts;
    }


    public int getSignFeedbackStatus() {
        return sign_feedback_stauts;
    }

    public void setCaptureId(int capture_id) {
        this.capture_id = capture_id;
    }

    public int getCaptureId() {
        return capture_id;
    }

    public void setCaptureComplete(boolean status) {
        is_capture_complete = status;
    }

    public boolean isCaptureComplete() {
        return is_capture_complete;
    }

}
