package com.github.scarecrow.signscognizing.Utilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    private OnUpdateComplete updateCallback;

    private static ArmbandManager instance = new ArmbandManager();

    private List<Armband> armband_list = new ArrayList<>();

    private boolean armband_pair_mode = false;
    // true 双手 false 单手

    private Armband current_connected_armband_single;

    private Armband current_connected_armband_right;
    private Armband current_connected_armband_left;


    public static ArmbandManager getArmbandsManger() {
        return instance;
    }

    public void updateArmbandsList() {
//        new FetchArmbandsList().execute();
        armband_list.add(new Armband("{\n" +
                "\"armband_id\": \"armband 0\",\n" +
                "\"armband_status\": 0\n" +
                "}"));
        armband_list.add(new Armband("{\n" +
                "\"armband_id\": \"armband 0\",\n" +
                "\"armband_status\": 0\n" +
                "}"));
        armband_list.add(new Armband("{\n" +
                "\"armband_id\": \"armband 0\",\n" +
                "\"armband_status\": 0\n" +
                "}"));
        armband_list.add(new Armband("{\n" +
                "\"armband_id\": \"armband 0\",\n" +
                "\"armband_status\": 0\n" +
                "}"));
        updateCallback.noticeUpdateComplete(armband_list);

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


    public void setOnUpdateCompleteCallback(OnUpdateComplete onUpdateComplete) {
        updateCallback = onUpdateComplete;
    }

    public boolean getArmbandPairMode() {
        return armband_pair_mode;
    }

    public void setArmbandPairMode(boolean mode) {
        armband_pair_mode = mode;
    }


    public void setCurrentConnectedArmband(Armband armband) {
        if (!armband_pair_mode)
            current_connected_armband_single = armband;
        else
            Log.e(TAG, "setCurrentConnectedArmband: pair mode cant match");
    }

    public void setCurrentConnectedArmband(Armband left, Armband right) {
        if (armband_pair_mode) {
            current_connected_armband_left = left;
            current_connected_armband_right = right;
        } else
            Log.e(TAG, "setCurrentConnectedArmband: pair mode cant match");
    }

    public void releasePairedArmbands() {
        current_connected_armband_single =
                current_connected_armband_left =
                        current_connected_armband_right = null;
    }


    public Armband[] getCurrentConnectedArmband() {
        if (armband_pair_mode)
            return new Armband[]{current_connected_armband_left, current_connected_armband_right};
        else
            return new Armband[]{current_connected_armband_single};
    }


    private class FetchArmbandsList extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... param) {

            OkHttpClient okHttpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .get()
                    .url(SERVER_IP_ADDRESS + "/get_armbands_list/")
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    parseArmbandsListJSON(res);
                    return true;
                }
            } catch (Exception ee) {
                Log.e(TAG, "FetchArmbandsList error: " + ee);
                ee.printStackTrace();
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean response) {
            if (response) {
                updateCallback.noticeUpdateComplete(armband_list);
                this.cancel(false);
            }
        }
    }

    public interface OnUpdateComplete {
        void noticeUpdateComplete(List<Armband> armbandList);
    }
}
