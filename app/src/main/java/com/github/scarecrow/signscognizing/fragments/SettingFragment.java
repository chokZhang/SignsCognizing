package com.github.scarecrow.signscognizing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.ArmbandManager;
import com.github.scarecrow.signscognizing.activities.MainActivity;

/**
 * Created by Scarecrow on 2018/2/27.
 */

public class SettingFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container,
                false);
        Switch double_hand_switch = view.findViewById(R.id.switch_double_hand);
        double_hand_switch.setChecked(ArmbandManager.getArmbandsManger().getArmbandPairMode());

        double_hand_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ArmbandManager.getArmbandsManger().setArmbandPairMode(isChecked);
            }
        });

        Button button = view.findViewById(R.id.button_setting_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity parent_activity = (MainActivity) getActivity();
                parent_activity.switchFragment(MainActivity.FRAGMENT_START_CONTROL);
            }
        });
        return view;
    }
}
