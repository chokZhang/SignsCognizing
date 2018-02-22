package com.github.scarecrow.signscognizing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.ArmbandManager;
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
        Button bt = view.findViewById(R.id.button_input_panel_back);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketConnectionManager.getInstance()
                        .disconnect();
                MainActivity activity = (MainActivity) getActivity();
                activity.switchFragment(MainActivity.FRAGMENT_ARMBANDS_SELECT);
                activity.switchFragment(MainActivity.FRAGMENT_INFO_DISPLAY);
            }
        });

        //手语输入
        bt = view.findViewById(R.id.button_input_panel_sign_start);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketConnectionManager.getInstance()
                        .sendMessage(buildSignRecognizeRequest(0));
                MessageManager.getInstance()
                        .buildSignMessage();
            }
        });

        //语音输入
    }

    /**
     * 手语识别请求体构造
     * 如果是新增识别， 的 sign_id字段使用0 标识
     *  如："data": {"sign_id" :0}
     * @return 请求的json
     */
    public static String buildSignRecognizeRequest(int sign_id) {
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

    @Override
    public void onStop() {
        View view = getView();
        VoiceRecordButton voiceRecordButton = (VoiceRecordButton)
                view.findViewById(R.id.button_input_panel_voice_start);
        voiceRecordButton.releaseMediaResource();
        super.onStop();
    }
}
