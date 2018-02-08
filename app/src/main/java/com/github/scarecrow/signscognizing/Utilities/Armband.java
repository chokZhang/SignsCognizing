package com.github.scarecrow.signscognizing.Utilities;

import android.util.Log;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/6.
 * 手环对象 从服务器拉取
 */

public class Armband {
    public static final int ARMBAND_READY = -1,
            ARMBAND_OCCURPIED = -2;


    private String armband_id;

    private int armband_status;

    public Armband(JSONObject jsonObject) {
        try {
            armband_id = jsonObject.getString("armband_id");
            armband_status = jsonObject.getInt("armband_status");
        } catch (Exception ee) {
            Log.e(TAG, "Armband constructing : error in parse object json : " + ee);
            ee.printStackTrace();
        }
    }

    public Armband(String raw_json) {
        try {
            JSONObject jsonObject = new JSONObject(raw_json);
            armband_id = jsonObject.getString("armband_id");
            armband_status = jsonObject.getInt("armband_status");
        } catch (Exception ee) {
            Log.e(TAG, "Armband constructing : error in parse object json : " + ee);
            ee.printStackTrace();
        }
    }

    public int getArmband_status() {
        return armband_status;
    }

    public String getArmband_id() {
        return armband_id;
    }

    @Override
    public String toString() {
        String str, armband_status_str;
        if (armband_status == ARMBAND_OCCURPIED)
            armband_status_str = "armband occupied";
        else
            armband_status_str = "armband ready to connect";

        str = "armband_id:\n   " + armband_id
                + "\narmband_status:\n   " + armband_status_str;
        return str;
    }
}
