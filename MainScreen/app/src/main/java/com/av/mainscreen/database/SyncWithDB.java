package com.av.mainscreen.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.av.mainscreen.constants.STRINGS;

/**
 * Created by Ankit on 18-11-2018.
 */

public class SyncWithDB {
    private static Context ctx;
    private static SharedPreferences sharedpreferences;

    public SyncWithDB(Context context) {
        this.ctx = context;
        sharedpreferences = context.getSharedPreferences(STRINGS.SETTINGS, Context.MODE_PRIVATE);
    }

    public static void firstUse(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("dummy", "value");
        editor.commit();
    }

    public static void putSettingsInDB(){

    }

    public static void extractSettingsFromDB(){

    }
}
