package com.av.mainscreen.constants;

/**
 * Created by Ankit on 17-11-2018.
 */

public class STRINGS {
    public static final String SETTINGS = "SETTINGS";/*will be changed later on*/

    public static final String COMMON_SETTING = "COMMON_SETTING";
    public static final String MAC_ADDRESS = "MAC";

    public static final String CALL_SETTING = "CALL_SETTING";

    public static final String ONE_TAP = "ONE_TAP";
    public static final String TWO_TAP = "TWO_TAP";
    public static final String THREE_TAP = "THREE_TAP";

    public static String[] INTENT_FILTER_SONG = {
            "com.android.music.musicservicecommand",
            "com.android.music.metachanged",
            "com.android.music.playstatechanged",
            "com.android.music.updateprogress",
            "com.android.music.metachanged",
            "com.htc.music.metachanged",
            "fm.last.android.metachanged",
            "com.sec.android.app.music.metachanged",
            "com.nullsoft.winamp.metachanged",
            "com.amazon.mp3.metachanged",
            "com.miui.player.metachanged",
            "com.real.IMP.metachanged",
            "com.sonyericsson.music.metachanged",
            "com.rdio.android.metachanged",
            "com.samsung.sec.android.MusicPlayer.metachanged",
            "com.andrew.apollo.metachanged",
    };

    public static String[] INTENT_FILTER_CALL = {
            "android.intent.action.PHONE_STATE",
            "android.intent.action.NEW_OUTGOING_CALL"
    };
}
