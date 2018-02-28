package com.github.scarecrow.signscognizing.Utilities;

import android.util.Log;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/6.
 * 手环对象 数据从服务器拉取
 */

public class Armband {
    public static final int ARMBAND_READY = -1,
            ARMBAND_OCCURPIED = -2;

    private String armband_id;

    private int armband_occupy_status;


    /**
     * 0 未匹配
     * 1 作为左手
     * 2 作为右手
     */
    public static int NO_PAIR = 0,
            PAIR_LEFT_HAND = 1,
            PAIR_RIGHT_HAND = 2;

    private int armband_pair_status = 0;

    public Armband(JSONObject jsonObject) {
        try {
            armband_id = jsonObject.getString("armband_id");
            armband_occupy_status = jsonObject.getInt("armband_status");
        } catch (Exception ee) {
            Log.e(TAG, "Armband constructing : error in parse object json : " + ee);
            ee.printStackTrace();
        }
    }

    public Armband(String raw_json) {
        try {
            JSONObject jsonObject = new JSONObject(raw_json);
            armband_id = jsonObject.getString("armband_id");
            armband_occupy_status = jsonObject.getInt("armband_status");
        } catch (Exception ee) {
            Log.e(TAG, "Armband constructing : error in parse object json : " + ee);
            ee.printStackTrace();
        }
    }

    public int getArmbandStatus() {
        return armband_occupy_status;
    }

    public String getArmbandId() {
        return armband_id;
    }


    public void setPairStatus(int status) {
        armband_pair_status = status;
    }

    public int getPairStatus() {
        return armband_pair_status;
    }
    @Override
    public String toString() {
        String str, armband_status_str;
        if (armband_occupy_status == ARMBAND_OCCURPIED)
            armband_status_str = "手环已被其他终端使用";
        else
            armband_status_str = "手环就绪，准备接受连接";

        str = "手环ID:\n   " + armband_id
                + "\n手环当前状态:\n   " + armband_status_str;
        return str;
    }
}
