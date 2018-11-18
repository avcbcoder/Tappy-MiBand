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

    /**
     * Put all data from SETTINGS to DB
     */
    public static void putSettingsInDB() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(STRINGS.MAC_ADDRESS, SETTINGS.MAC_ADDRESS);
        editor.putString(STRINGS.ONE_TAP,SETTINGS.taps[1].getStringForm());
        editor.putString(STRINGS.TWO_TAP,SETTINGS.taps[2].getStringForm());
        editor.putString(STRINGS.THREE_TAP,SETTINGS.taps[3].getStringForm());
        editor.putString(STRINGS.COMMON_SETTING,SETTINGS.COMMON_SETTING.getStringForm());
        editor.putString(STRINGS.CALL_SETTING,SETTINGS.CALL.getStringForm());
        editor.commit();
    }

    /**
     * There will be just 6 strings stored in sharedPrefs
     */
    public static void extractSettingsFromDB() {
        String macAddress = sharedpreferences.getString(STRINGS.MAC_ADDRESS, "");
        String oneTap = sharedpreferences.getString(STRINGS.ONE_TAP, SETTINGS.DEF_ONE_TAP);
        String twoTap = sharedpreferences.getString(STRINGS.TWO_TAP, SETTINGS.DEF_TWO_TAP);
        String threeTap = sharedpreferences.getString(STRINGS.THREE_TAP, SETTINGS.DEF_THREE_TAP);
        String callSetting = sharedpreferences.getString(STRINGS.CALL_SETTING, SETTINGS.DEF_CALL_SETTING);
        String commonSetting = sharedpreferences.getString(STRINGS.COMMON_SETTING, SETTINGS.DEF_COMMON_SETTING);

        /*set MAC*/
        SETTINGS.MAC_ADDRESS = macAddress;

        /*update common settings*/
        SETTINGS.COMMON_SETTING.setFromString(commonSetting);

        /*update call settings*/
        SETTINGS.CALL.setFromString(callSetting);

        /*update tap settings*/
        SETTINGS.taps[1] = new SETTINGS.TAP(oneTap);
        SETTINGS.taps[2] = new SETTINGS.TAP(twoTap);
        SETTINGS.taps[3] = new SETTINGS.TAP(threeTap);
    }

}
