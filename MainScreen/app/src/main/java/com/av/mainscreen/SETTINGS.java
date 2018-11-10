package com.av.mainscreen;

/**
 * Created by Ankit on 10-11-2018.
 */

public class SETTINGS {
    public static boolean CONNECT_BLUETOOTH_TRIGGER;
    public static boolean CONNECT_HEADPHONE_PLUGGED;
    public static boolean DISCONNECT_HEADPHONE_REMOVED;
    public static boolean IGNORE_FIRST_TAP;
    public static boolean KEEP_RUNNING;
    public static int DELAY_TAP;

    public static TAP[] taps = new TAP[4];

    public static class TIMER {
        public static boolean INTERMEDIATE_VIBRATION;
        public static int TIME;// in milliseconds
        public static int VIBRATION_AT_END;// -1 if doesn't
        public static int MESSAGE_AT_END;
        public static int MESSAGE_AT_START;
    }

    public static class Call {
        public static boolean ENABLE;
        public static int ONE_TAP; // 0->Nothing 1->Mute 2->Reply
        public static int DOUBLE_TAP; // 0->Nothing 1->Mute 2->Reply
        public static String TEXT; // Text to send as reply to caller
        public static String DEF_TEXT = "Can't talk to you right now! Call me later?";
    }

    public static class TAP {
        public static boolean SWITCH;
        public static boolean VOL_INC;
        public static boolean VOL_DEC;
        public static boolean NEXT;
        public static boolean PREV;
        public static boolean PLAY_PAUSE;
        public static boolean VIBRATE;
        public static int VIBRATE_DELAY;
        public static boolean TIMER;
        public static boolean CAMERA;
    }
}
