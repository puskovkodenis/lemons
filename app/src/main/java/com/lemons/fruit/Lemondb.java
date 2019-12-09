package com.lemons.fruit;

import android.content.Context;
import android.content.SharedPreferences;

public class Lemondb {
    private static String lemon = "lemon";
    private SharedPreferences preferences;

    public Lemondb(Context context){
        String NAME = "lemon";
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public void setLemon(String data){
        preferences.edit().putString(Lemondb.lemon, data).apply();
    }

    public String getLemon(){
        return preferences.getString(lemon, "");
    }
}
