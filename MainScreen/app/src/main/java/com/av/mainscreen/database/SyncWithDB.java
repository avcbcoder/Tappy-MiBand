package com.av.mainscreen.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.av.mainscreen.constants.SETTINGS;
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

    public static void firstUse() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("dummy", "value");
        editor.commit();
    }

    public static void putSettingsInDB() {

    }

    /**
     * There will be
     */
    public static void extractSettingsFromDB() {

    }

    public static SETTINGS.TAP getTap(int t) {
        String storedString = "";

        if(t==1)
            storedString = sharedpreferences.getString(STRINGS.ONE_TAP, SETTINGS.DEF_ONE_TAP);
        else if(t==2)
            storedString = sharedpreferences.getString(STRINGS.TWO_TAP, SETTINGS.DEF_TWO_TAP);
        else if(t==3)
            storedString = sharedpreferences.getString(STRINGS.THREE_TAP, SETTINGS.DEF_THREE_TAP);
        else
            return null;

        return new SETTINGS.TAP(storedString);
    }
}
