package com.github.scarecrow.signscognizing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.scarecrow.signscognizing.MainActivity;
import com.github.scarecrow.signscognizing.R;

/**
 * Created by Scarecrow on 2018/2/5.
 */

public class ArmbandSelectFragment extends Fragment {
    private View fragment_view ;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_armband_select, container,
                false);

        fragment_view = view;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Button bt = fragment_view.findViewById(R.id.button_armband_select_return);
        final MainActivity parent_activity = (MainActivity)getActivity();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent_activity.fragmentSwitch(MainActivity.FRAGMENT_START_CONTROL);
            }
        });
    }

}
