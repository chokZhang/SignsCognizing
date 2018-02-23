package com.github.scarecrow.signscognizing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.MessageManager;
import com.github.scarecrow.signscognizing.Utilities.TextMessage;
import com.github.scarecrow.signscognizing.adapters.ConversationMessagesRVAdapter;

/**
 * Created by Scarecrow on 2018/2/5.
 *
 */

public class ConversationDisplayFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coversation_display, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        final RecyclerView recyclerView = view.findViewById(R.id.conversation_display_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final ConversationMessagesRVAdapter adapter = new ConversationMessagesRVAdapter();
        recyclerView.setAdapter(adapter);
        MessageManager.getInstance().addNewNoticeTarget(new MessageManager.NoticeMessageChanged() {
            @Override
            public void onNewMessageAdd() {
                adapter.updateMessageList();
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onMessageContentChange() {
                adapter.updateMessageList();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSignCaptureEnd() {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSignCaptureStart() {
                adapter.notifyDataSetChanged();
            }

        });
        //发送消息按钮
        Button bt = view.findViewById(R.id.button_text_send);
        final EditText editText = view.findViewById(R.id.conversation_text_input);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editText.getText().toString();
                MessageManager.getInstance().buildTextMessage(text);
                editText.setText("");
            }
        });

    }

}
