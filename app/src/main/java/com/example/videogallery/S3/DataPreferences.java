package com.example.videogallery.S3;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataPreferences {

    private SharedPreferences preferences;
    public static final String KEY_SAVE_URLS = "cacheUrls";
    public static final String ARE_URLS_CACHED = "areUrlsCached";
    public static final String KEY_SAVE_Keys = "cacheKeys";
    public static final String ARE_KEYS_CACHED = "areKeysCached";

    public DataPreferences(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void savePreferences(ArrayList<String> urls, String key) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(urls);
        if(key==KEY_SAVE_URLS) {
            editor.putString(KEY_SAVE_URLS, json);
            editor.putBoolean(ARE_URLS_CACHED, true);
        } else {
            editor.putString(KEY_SAVE_Keys, json);
            editor.putBoolean(ARE_KEYS_CACHED, true);
        }
        editor.apply();
    }

    public Boolean arePreferencesCached(String key) {
        if(key == ARE_URLS_CACHED) {
            return preferences.getBoolean(ARE_URLS_CACHED, false);
        } else {
            return preferences.getBoolean(ARE_KEYS_CACHED, false);
        }
    }

    public ArrayList<String> getSavedPreferences(String key) {
        Gson gson = new Gson();
        String json;
        if(key == KEY_SAVE_URLS) {
             json = preferences.getString(KEY_SAVE_URLS, null);
        } else {
            json = preferences.getString(KEY_SAVE_Keys, null);
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
