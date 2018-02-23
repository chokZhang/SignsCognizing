package com.github.scarecrow.signscognizing.adapters;

import android.graphics.Color;

import com.github.scarecrow.signscognizing.Utilities.SocketConnectionManager;
import com.github.scarecrow.signscognizing.fragments.InputControlPanelFragment;
import com.iflytek.cloud.SpeechRecognizer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.ConversationMessage;
import com.github.scarecrow.signscognizing.Utilities.MessageManager;
import com.github.scarecrow.signscognizing.Utilities.SignMessage;
import com.github.scarecrow.signscognizing.Utilities.TextMessage;
import com.github.scarecrow.signscognizing.Utilities.VoiceMessage;

import java.io.InputStreamReader;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/10.
 */

public class ConversationMessagesRVAdapter extends RecyclerView.Adapter<ConversationMessagesRVAdapter.MessagesItemViewHolder> {

    private List<ConversationMessage> messages_list;

    public ConversationMessagesRVAdapter() {
        updateMessageList();
    }

    public void updateMessageList() {
        messages_list = MessageManager.getInstance().getMessagesList();
    }

    static class MessagesItemViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout receive_msg_view, send_msg_view,
                sign_confirm_dialog, sign_recapture_dialog;

        public TextView receive_msg_content, send_msg_content,
                sign_confirm_yes_button, sign_confirm_no_button,
                sign_recapture_yes_button, sign_recapture_no_button,
                msg_type_display;


        public MessagesItemViewHolder(View view) {
            super(view);
            receive_msg_view = view.findViewById(R.id.message_receive_view);
            send_msg_view = view.findViewById(R.id.message_send_view);
            sign_confirm_dialog = view.findViewById(R.id.sign_confirm_dialog);
            sign_recapture_dialog = view.findViewById(R.id.sign_recapture_dialog);

            receive_msg_content = view.findViewById(R.id.msg_content_receive);
            send_msg_content = view.findViewById(R.id.msg_content_send);

            sign_confirm_yes_button = view.findViewById(R.id.button_sign_confirm_yes);
            sign_confirm_no_button = view.findViewById(R.id.button_sign_confirm_no);

            sign_recapture_yes_button = view.findViewById(R.id.button_sign_recapture_yes);
            sign_recapture_no_button = view.findViewById(R.id.button_sign_recapture_no);

            msg_type_display = view.findViewById(R.id.text_view_msg_type_display);

        }

    }

    @Override
    public MessagesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_message_item, parent, false);
        return new MessagesItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessagesItemViewHolder holder, int position) {
        ConversationMessage message = messages_list.get(position);
        initializeHolderView(holder);
        // 每个手语是否已经重发保存一个状态 保存于数据中
        // view是重用的 每次都要先初始化  然后跟着数据的情况改变
        switch (message.getMsgType()) {
            case ConversationMessage.SIGN:
                final SignMessage signMessage = (SignMessage) message;
                //如果这条手势消息没有被确认 则保持可被确认的初始状态 随时被请求重新采集
                setHolderViewByMsgState(holder, signMessage);
                break;

            case ConversationMessage.TEXT:
                TextMessage text_message = (TextMessage) message;
                holder.send_msg_view.setVisibility(View.VISIBLE);
                holder.receive_msg_view.setVisibility(View.GONE);
                holder.send_msg_content.setText(text_message.getTextContent());
                holder.msg_type_display.setText("文字消息");
                break;

            case ConversationMessage.VOICE:
                VoiceMessage voice_message = (VoiceMessage) message;
                holder.send_msg_view.setVisibility(View.VISIBLE);
                holder.receive_msg_view.setVisibility(View.GONE);
                holder.send_msg_content.setText(voice_message.getTextContent());
                holder.msg_type_display.setText("语音消息");
                break;
            default:
                break;
        }

    }

    private void initializeHolderView(MessagesItemViewHolder holder) {
        holder.send_msg_view.setVisibility(View.GONE);
        holder.receive_msg_view.setVisibility(View.GONE);
        holder.sign_recapture_dialog.setVisibility(View.GONE);
        holder.sign_confirm_dialog.setVisibility(View.GONE);
        int init_blue_color_value = 0xFF3F51B5;
        holder.sign_confirm_yes_button.setTextColor(init_blue_color_value);
        holder.sign_confirm_no_button.setTextColor(init_blue_color_value);
        holder.sign_recapture_yes_button.setTextColor(init_blue_color_value);
        holder.sign_recapture_no_button.setTextColor(init_blue_color_value);
        View.OnClickListener empty = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
        holder.sign_recapture_no_button.setOnClickListener(empty);
        holder.sign_recapture_yes_button.setOnClickListener(empty);
        holder.sign_confirm_no_button.setOnClickListener(empty);
        holder.sign_confirm_yes_button.setOnClickListener(empty);

    }

    private void setHolderViewByMsgState(final MessagesItemViewHolder holder,
                                         final SignMessage message) {
        initializeHolderView(holder);
        switch (message.getSignFeedbackStatus()) {
            case SignMessage.INITIAL:
                holder.receive_msg_view.setVisibility(View.VISIBLE);
                holder.receive_msg_content.setText(message.getTextContent());
                if (message.isCaptureComplete()) {
                    holder.sign_confirm_dialog.setVisibility(View.VISIBLE);
                    holder.sign_confirm_yes_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.sign_confirm_yes_button.setTextColor(Color.GRAY);
                            message.setSignFeedbackStatus(SignMessage.CONFIRMED_CORRECT);
                            //todo 根据capture_id反馈正确
                            setHolderViewByMsgState(holder, message);
                        }
                    });

                    holder.sign_confirm_no_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            message.setSignFeedbackStatus(SignMessage.CONFIRMED_WRONG);
                            setHolderViewByMsgState(holder, message);

                        }
                    });
                }
                break;
            case SignMessage.CONFIRMED_CORRECT:
                holder.receive_msg_view.setVisibility(View.VISIBLE);
                holder.sign_confirm_dialog.setVisibility(View.VISIBLE);
                holder.receive_msg_content.setText(message.getTextContent());
                holder.sign_confirm_yes_button.setTextColor(Color.GRAY);
                break;
            case SignMessage.CONFIRMED_WRONG:
                holder.receive_msg_view.setVisibility(View.VISIBLE);
                holder.sign_confirm_dialog.setVisibility(View.VISIBLE);
                holder.receive_msg_content.setText(message.getTextContent());
                holder.sign_confirm_no_button.setTextColor(Color.GRAY);

                holder.sign_recapture_dialog.setVisibility(View.VISIBLE);
                holder.sign_recapture_yes_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: 手语re采集 回调");
                        holder.sign_recapture_yes_button.setTextColor(Color.GRAY);
                        message.setSignFeedbackStatus(SignMessage.INITIAL);
                        recaptureRequest(message);
                        setHolderViewByMsgState(holder, message);

                    }
                });
                holder.sign_recapture_no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.sign_recapture_no_button.setTextColor(Color.GRAY);
                        message.setSignFeedbackStatus(SignMessage.NO_RECAPTURE);
                        setHolderViewByMsgState(holder, message);
                    }
                });
                break;
            case SignMessage.NO_RECAPTURE:
                holder.receive_msg_view.setVisibility(View.VISIBLE);
                holder.sign_confirm_dialog.setVisibility(View.VISIBLE);
                holder.receive_msg_content.setText(message.getTextContent());
                holder.sign_confirm_no_button.setTextColor(Color.GRAY);

                holder.sign_recapture_dialog.setVisibility(View.VISIBLE);
                holder.sign_recapture_no_button.setTextColor(Color.GRAY);
                break;
        }
    }

    private void recaptureRequest(SignMessage msg) {
        MessageManager.getInstance()
                .recaptureSignRequest(msg);
    }

    @Override
    public int getItemCount() {

        return messages_list.size();
    }
}
