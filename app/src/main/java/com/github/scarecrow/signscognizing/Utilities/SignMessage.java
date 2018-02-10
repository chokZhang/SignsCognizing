package com.github.scarecrow.signscognizing.Utilities;

/**
 * Created by Scarecrow on 2018/2/6.
 */

public class SignMessage extends ConversationMessage {
    public static final int HAD_CONFIRMED = 564,
            INITIAL = 2541;

    private String middle_symbols;

    private int sign_feedback_stauts;

    private Armband capture_armband;

    public SignMessage(String text, String middle_symbols, int msg_id, Armband capture_armband) {
        super(msg_id, ConversationMessage.SIGN, text);
        this.middle_symbols = middle_symbols;
        this.capture_armband = capture_armband;
        sign_feedback_stauts = INITIAL;
    }

    public void setSignFeedbackStauts(int stauts) {
        sign_feedback_stauts = stauts;
    }

    public int getSignFeedbackStatus() {
        return sign_feedback_stauts;
    }

    public String getSignMiddleSymbols() {
        return middle_symbols;
    }
}
