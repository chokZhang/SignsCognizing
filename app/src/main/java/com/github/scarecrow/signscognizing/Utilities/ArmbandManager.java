package com.github.scarecrow.signscognizing.Utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/6.
 *
 */

public class ArmbandManager {

    public static String SERVER_IP_ADDRESS = "http://192.168.0.102:8000/app";

    private ArmbandManager() {

    }

    private static ArmbandManager instance = new ArmbandManager();

    private List<Armband> armband_list = new ArrayList<>();

    private Armband current_connected_armband;


    public static ArmbandManager getArmbandsManger() {
        return instance;
    }

    public void updateArmbandsList() {

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url(SERVER_IP_ADDRESS + "/get_armbands_list/")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: error in  fetchArmbandList " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    // okhttp 中的response.body().string() 只能调用一次
                    Log.d(TAG, "onResponse: get armbandsss : " + res);
                    parseArmbandsListJSON(res);
                }
            }
        });
    }

    public List<Armband> getArmbandsList() {
        updateArmbandsList();
        return armband_list;
    }


    private void parseArmbandsListJSON(String armband_list_JSON) {
        armband_list.clear();
        try {
            JSONArray armbands_json_list = new JSONArray(armband_list_JSON);
            for (int i = 0; i < armbands_json_list.length(); i++) {
                JSONObject jsonObject = armbands_json_list.getJSONObject(i);
                armband_list.add(new Armband(jsonObject));
            }
        } catch (Exception ee) {
            Log.e(TAG, "getArmbandsList: cannot parse armbands list json " + ee);
            ee.printStackTrace();
        }
    }


    public void setCurrentConnectedArmband(Armband armband) {
        current_connected_armband = armband;
    }

    public Armband getCurrentConnectedArmband() {
        return current_connected_armband;
    }
}
