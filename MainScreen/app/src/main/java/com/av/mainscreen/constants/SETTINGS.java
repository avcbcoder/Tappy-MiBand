package com.av.mainscreen.constants;

import java.util.Arrays;

/**
 * Created by Ankit on 10-11-2018.
 */

public class SETTINGS {
    public static boolean CONNECT_BLUETOOTH_TRIGGER;
    public static boolean CONNECT_HEADPHONE_PLUGGED;
    public static boolean DISCONNECT_HEADPHONE_REMOVED;
    public static boolean IGNORE_FIRST_TAP;
    public static boolean KEEP_RUNNING;
    public static int DELAY_TAP = 500;
    public static int CLICK_INTERVAL = 1150; // A->600, B->1150, C->1700
    public static int DIFF_BTW_MULTIPLE_COMMANDS = 1000; // A->500, B->1000, C->1500
    public static String MAC_ADDRESS = "E5:E7:F4:75:6F:C1";

    public static TAP[] taps = {new TAP(),new TAP(),new TAP(),new TAP()};

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
        public static String TEXT = ""; // Text to send as reply to caller
        public static String DEF_TEXT = "Can't talk to you right now! Call me later?";
    }

    public static class TAP {
        public boolean SWITCH;
        public boolean VOL_INC;
        public boolean VOL_DEC;
        public boolean NEXT;
        public boolean PREV;
        public boolean PLAY_PAUSE;
        public boolean VIBRATE=false;
        public int VIBRATE_DELAY = 300;
        public boolean TIMER;
        public boolean CAMERA;
        public int CALL;// 0->Nothing 1->Mute 2->Reply || only for two taps
    }
}
