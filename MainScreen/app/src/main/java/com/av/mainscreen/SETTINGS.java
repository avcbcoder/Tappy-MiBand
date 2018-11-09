package com.av.mainscreen;

/**
 * Created by Ankit on 10-11-2018.
 */

public class SETTINGS {
    public static boolean CONNECT_BLUETOOTH_TRIGGER;
    public static boolean CONNECT_HEADPHONE_PLUGGED;
    public static boolean DISCONNECT_HEADPHONE_REMOVED;
    public static int DELAY_TAP;

    public static TAP[] taps = new TAP[4];

    public static class TIMER {
        public static int TIME;// in milliseconds
        public static int VIBRATION_AT_END;
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
