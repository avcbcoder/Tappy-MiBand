package com.av.mainscreen.constants;

/**
 * Created by Ankit on 10-11-2018.
 */

public class SETTINGS {
    // [1, 1, 0, 1, 1150, 1000]
    public static class COMMON_SETTING {
        public static boolean CONNECT_BLUETOOTH_TRIGGER;
        public static boolean CONNECT_HEADPHONE_PLUGGED;
        public static boolean DISCONNECT_HEADPHONE_REMOVED;
        public static boolean KEEP_RUNNING;
        public static int CLICK_INTERVAL = 1150; // A->600, B->1150, C->1700
        public static int DELAY_BTW_MULTIPLE_COMMANDS = 1000; // A->500, B->1000, C->1500

        public static String getStringForm() {
            return String.format("%d %d %d %d %d %d",
                    (CONNECT_BLUETOOTH_TRIGGER ? 1 : 0),
                    (CONNECT_HEADPHONE_PLUGGED ? 1 : 0),
                    (DISCONNECT_HEADPHONE_REMOVED ? 1 : 0),
                    (KEEP_RUNNING ? 1 : 0),
                    CLICK_INTERVAL,
                    DELAY_BTW_MULTIPLE_COMMANDS);
        }

        public static void setFromString(String s) {
            String[] arr = s.split(" ");
            CONNECT_BLUETOOTH_TRIGGER = new Integer(arr[0]) == 1;
            CONNECT_HEADPHONE_PLUGGED = new Integer(arr[1]) == 1;
            DISCONNECT_HEADPHONE_REMOVED = new Integer(arr[2]) == 1;
            KEEP_RUNNING = new Integer(arr[3]) == 1;
            CLICK_INTERVAL = new Integer(arr[4]);
            DELAY_BTW_MULTIPLE_COMMANDS = new Integer(arr[5]);
        }
    }

    public static String MAC_ADDRESS = "E5:E7:F4:75:6F:C1";

    // [0, text]
    public static class CALL {
        public static boolean ENABLE;
        public static String TEXT = ""; // Text to send as reply to caller
        public static String DEF_TEXT = "Can't talk to you right now! CALL me later?";

        public static String getStringForm() {
            return String.format("%d (%s)", (ENABLE ? 1 : 0), TEXT);
        }

        public static void setFromString(String s) {
            ENABLE = s.charAt(0) == 1;
            TEXT = s.substring(3, s.length() - 1);
        }
    }

    // [vol, music, vibrate, delay, repeat, call]
    public static final String DEF_ONE_TAP = "0 1 1 300 1 1";
    public static final String DEF_TWO_TAP = "0 2 1 300 1 1";
    public static final String DEF_THREE_TAP = "0 3 1 300 1 1";
    public static final String DEF_COMMON_SETTING = "1 1 0 1 1150 1000";
    public static final String DEF_CALL_SETTING = "0 (Can't talk to you right now! CALL me later?)";

    public static TAP[] taps = {null, new TAP(DEF_ONE_TAP), new TAP(DEF_TWO_TAP), new TAP(DEF_THREE_TAP)};

    public static class TAP {
        public boolean SWITCH;
        public int VOL; // 0->Nothing 1->Inc 2->Dec
        public int MUSIC; // 0->Nothing 1->Next 2-> Prev 3-> play/pause
        public int VIBRATE; // 0-> yes 1->no
        public int VIBRATE_DELAY;
        public int VIBRATE_REPEAT;
        public int CALL;// 0->Nothing 1->Mute 2->Reply || only for two taps
        public boolean TIMER;
        public boolean CAMERA;

        public TAP(String from) {
            String[] arr = from.split(" ");
            this.VOL = new Integer(arr[0]);
            this.MUSIC = new Integer(arr[1]);
            this.VIBRATE = new Integer(arr[2]);
            this.VIBRATE_DELAY = new Integer(arr[3]);
            this.VIBRATE_REPEAT = new Integer(arr[4]);
            this.CALL = new Integer(arr[5]);
        }

        public String getStringForm() {
            return String.format("%d %d %d %d %d %d", VOL, MUSIC, VIBRATE, VIBRATE_DELAY, VIBRATE_REPEAT, CALL);
        }
    }

    public static class TIMER {
        public static boolean INTERMEDIATE_VIBRATION;
        public static int TIME;// in milliseconds
        public static int VIBRATION_AT_END;// -1 if doesn't
        public static int MESSAGE_AT_END;
        public static int MESSAGE_AT_START;
    }
}
