package com.github.scarecrow.signscognizing.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.scarecrow.signscognizing.Utilities.Armband;
import com.github.scarecrow.signscognizing.Utilities.SocketConnectionManager;
import com.github.scarecrow.signscognizing.activities.MainActivity;
import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.ArmbandManager;
import com.github.scarecrow.signscognizing.adapters.ArmbandListRecyclerViewAdapter;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/5.
 *
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
                parent_activity.switchFragment(MainActivity.FRAGMENT_START_CONTROL);
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        final RecyclerView armbands_rv = fragment_view.findViewById(R.id.armbands_list_rv);
        armbands_rv.setLayoutManager(linearLayoutManager);
        final ArmbandListRecyclerViewAdapter adapter = new ArmbandListRecyclerViewAdapter(
                ArmbandManager.getArmbandsManger().getArmbandsList());
        adapter.setOnListItemClickListenner(new ListItemClickListenner() {
            @Override
            public void onListItemClick(Armband item) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("连接中");
                progressDialog.show();

                SocketConnectionManager.getInstance()
                        .startConnection(item, new SocketConnectionManager.TaskCompleteCallback() {
                            @Override
                            public void onConnectSucceeded() {
                                progressDialog.cancel();
                                Toast.makeText(getContext(), " 连接成功", Toast.LENGTH_SHORT)
                                        .show();

                                ((MainActivity) getActivity())
                                        .switchFragment(MainActivity.FRAGMENT_INPUT_CONTROL);
                                ((MainActivity) getActivity())
                                        .switchFragment(MainActivity.FRAGMENT_CONVERSATION_DISPLAY);
                                SocketConnectionManager.getInstance()
                                        .sendMessage("hi there, server");
                            }
                            @Override
                            public void onConnectFailed() {
                                progressDialog.cancel();
                                Toast.makeText(getContext(), " 连接失败", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onReceivedMessage(String message) {
                                Log.d(TAG, "onReceiveMessage: receive message from server: "
                                        + message);
                                SocketConnectionManager.getInstance()
                                        .sendMessage("end");
                            }
                        });
            }
        });
        armbands_rv.setAdapter(adapter);

        bt = fragment_view.findViewById(R.id.button_armband_select_refresh);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArmbandManager.getArmbandsManger().updateArmbandsList();
                armbands_rv.getAdapter().notifyDataSetChanged();
            }
        });


    }

    public interface ListItemClickListenner {
        void onListItemClick(Armband item);
    }

}
