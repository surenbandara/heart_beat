package com.krakendepp.heart_beat.DataBaseManager;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class PreferenceManager {
    private static final String PREF_NAME = "HeartBeatSharedPreference";
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void saveJson(String key, JSONObject jsonObject) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, jsonObject.toString());
        editor.apply();
    }

    public JSONObject getJson(String key) {
        String jsonString = sharedPreferences.getString(key, "{}");
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

