package com.github.scarecrow.signscognizing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.ArmbandManager;
import com.github.scarecrow.signscognizing.Utilities.MessageManager;
import com.github.scarecrow.signscognizing.Utilities.SocketConnectionManager;
import com.github.scarecrow.signscognizing.activities.MainActivity;

/**
 * Created by Scarecrow on 2018/2/
 *
 * 记得在退出的时候 结束SocketCommunicator线程
 * 通过给ConnectionManager发消息
 */

public class InputControlPanelFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_control_panel, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        //返回button
        Button bt = view.findViewById(R.id.button_input_panel_back);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                activity.switchFragment(MainActivity.FRAGMENT_ARMBANDS_SELECT);
                activity.switchFragment(MainActivity.FRAGMENT_INFO_DISPLAY);
                SocketConnectionManager.getInstance()
                        .disconnect();
            }
        });

        //手语输入
        bt = view.findViewById(R.id.button_input_panel_sign_start);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketConnectionManager.getInstance()
                        .sendMessage(buildSignRecognizeRequest());
            }
        });

        //语音输入
        bt = view.findViewById(R.id.button_input_panel_voice_start);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageManager.getInstance()
                        .buildVoiceMessage("hhhhh");
                //todo 这里发起语音识别
            }
        });
    }

    private String buildSignRecognizeRequest() {
        String request = "{ " +
                "\"control\": \"sign_cognize_request\" ," +
                "\"data\": \"" + ArmbandManager.getArmbandsManger()
                .getCurrentConnectedArmband()
                .getArmband_id() + "\" }";
        return request;
    }
}
