package com.av.googledev.model;

/**
 * Created by Ankit on 31-10-2018.
 */
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;

public class Battery {
    private static final String TAG = "Battery";
    public int mBatteryLevel;
    public int mCycles;
    public Calendar mLastCharged;
    public Status mStatus;

    public static Battery fromByte(byte[] b) {
        Log.e(TAG, "fromByte: " + Arrays.toString(b));
        Battery battery = new Battery();
        battery.mBatteryLevel = b[0];
        battery.mStatus = Status.fromByte(b[9]);
        battery.mLastCharged = Calendar.getInstance();

        battery.mLastCharged.set(Calendar.YEAR, b[1]+2000);
        battery.mLastCharged.set(Calendar.MONTH, b[2]);
        battery.mLastCharged.set(Calendar.DATE, b[3]);

        battery.mLastCharged.set(Calendar.HOUR_OF_DAY, b[4]);
        battery.mLastCharged.set(Calendar.MINUTE, b[5]);
        battery.mLastCharged.set(Calendar.SECOND, b[6]);

        battery.mCycles = 0xffff & (0xff & b[7] | (0xff & b[8]) << 8);
        Log.e(TAG, "fromByte: battery returned" );
        return battery;
    }

    @Override
    public String toString() {
        Log.e(TAG, "toString: WTF" );
        return "Level: "+mBatteryLevel+" Cycles: "+mCycles+" State: "+mStatus.toString()+" Last Charged: "+mLastCharged.toString();
//        return String.format("Level: %s Cycles: %s State: %s Last Charged: %s", mBatteryLevel, mCycles, mStatus.toString(), mLastCharged.toString());
    }

    static enum Status {
        LOW, FULL, CHARGING, NOT_CHARGING;

        public static Status fromByte(byte b) {
            switch (b) {
                case 1:
                    return LOW;
                case 2:
                    return CHARGING;
                case 3:
                    return FULL;
                case 4:
                    return NOT_CHARGING;

                default:
                    return null;
            }
        }
    }
}