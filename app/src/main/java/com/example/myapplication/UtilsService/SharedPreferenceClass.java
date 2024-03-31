package com.example.myapplication.UtilsService;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.myapplication.LoginActivity;

public class SharedPreferenceClass {
    private static final String USER_PREF = "user_todo";
    private static final String SHARED_PREF_NAME = "user_session";
    private SharedPreferences appShared;
    private SharedPreferences.Editor prefsEditor;
    private Context context;


    public SharedPreferenceClass(Context context) {
        appShared = context.getSharedPreferences(USER_PREF, Activity.MODE_PRIVATE);
        this.prefsEditor = appShared.edit();
    }

    // int
    public int getValue_int(String key) {
        return appShared.getInt(key, 0);
    }

    public void setValue_int(String key, int value) {
        prefsEditor.putInt(key, value).commit();
    }

    // string
    public String getValue_string(String key) {
        return appShared.getString(key, "");
    }

    public void setValue_string(String key, String value) {
        prefsEditor.putString(key, value).commit();
    }


    // boolean
    public boolean getValue_boolean(String key) {
        return appShared.getBoolean(key, false);
    }

    public void setValue_boolean(String key, boolean value) {
        prefsEditor.putBoolean(key, value).commit();
    }

    public void clear() {
        prefsEditor.clear().commit();
    }


    public void logout(){

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}


