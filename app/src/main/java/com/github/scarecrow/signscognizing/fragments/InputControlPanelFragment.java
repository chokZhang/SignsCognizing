package com.github.scarecrow.signscognizing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.MessageManager;
import com.github.scarecrow.signscognizing.Utilities.SocketConnectionManager;
import com.github.scarecrow.signscognizing.activities.MainActivity;
import com.github.scarecrow.signscognizing.adapters.VoiceRecordButton;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/
 *
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
        final Button bt = view.findViewById(R.id.button_input_panel_back);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageManager.getInstance().stopSignRecognize();
                SocketConnectionManager.getInstance().disconnect();
                MainActivity activity = (MainActivity) getActivity();
                activity.switchFragment(MainActivity.FRAGMENT_ARMBANDS_SELECT);
                activity.switchFragment(MainActivity.FRAGMENT_INFO_DISPLAY);
            }
        });

        final TextView textView = view.findViewById(R.id.recognize_model_display);
        textView.setText("离线识别");
        Switch switcher = view.findViewById(R.id.recognize_model_switch);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "onCheckedChanged: 切换至在线识别模式");
                    textView.setText("在线识别");
                    SocketConnectionManager.getInstance()
                            .sendMessage(buildRecognizeModeSwitchRequest("online"));
                } else {
                    Log.d(TAG, "onCheckedChanged: 切换至离线识别模式");
                    textView.setText("离线识别");
                    SocketConnectionManager.getInstance()
                            .sendMessage(buildRecognizeModeSwitchRequest("offline"));
                }
            }
        });


        //手语输入
        final ImageButton bt_cap = view.findViewById(R.id.button_input_panel_sign_start);
        final TextView cap_state = view.findViewById(R.id.sign_input_state_tv);
        bt_cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean capture_state = MessageManager.getInstance()
                        .isCapturingSign();
                if (!capture_state) {
                    boolean res = MessageManager.getInstance()
                            .requestCaptureSign();
                    if (res)
                        cap_state.setText("结束手语采集");

                } else {
                    boolean res = MessageManager.getInstance()
                            .stopSignRecognize();
                    if (res)
                        cap_state.setText("开始手语采集");
                }
            }
        });
        MessageManager.getInstance()
                .addNewNoticeTarget(new MessageManager.NoticeMessageChanged() {
                    @Override
                    public void onNewMessageAdd() {
                    }

                    @Override
                    public void onMessageContentChange() {
                    }

                    @Override
                    public void onSignCaptureEnd() {
                        cap_state.setText("开始手语采集");
                    }

                    @Override
                    public void onSignCaptureStart() {
                        cap_state.setText("结束手语采集");
                    }
                });
        //语音输入
    }

    private String buildRecognizeModeSwitchRequest(String mode) {
        JSONObject request_body = new JSONObject();
        try {
            request_body.accumulate("control", "switch_recognize_mode");
            JSONObject data = new JSONObject();
            data.accumulate("mode", mode);
            request_body.accumulate("data", data);
        } catch (Exception ee) {
            Log.e(TAG, "buildSignRecognizeRequest: on build request json " + ee);
            ee.printStackTrace();
        }
        return request_body.toString();
    }

    @Override
    public void onStop() {
        View view = getView();
        if (view != null) {
            VoiceRecordButton voiceRecordButton =
                    view.findViewById(R.id.button_input_panel_voice_start);
            voiceRecordButton.releaseMediaResource();
        }
        super.onStop();
    }
}
