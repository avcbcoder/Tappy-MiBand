package com.av.googledev.model;

import android.util.Log;

import java.util.Observable;

/**
 * Created by Ankit on 31-10-2018.
 */

public class MiBand extends Observable {
    private static final String TAG = "MiBand";
    public String mBTAddress;
    public int mSteps;
    public String mName;
    public Battery mBattery;
    public LeParams mLeParams;



    public void setName(String name) {
        mName = name;
        Log.e(TAG, "setName: "+ "setting "+name+" as BLE name" );
        setChanged();
        notifyObservers();
    }

    public void setSteps(int steps) {
        mSteps = steps;
        Log.e(TAG, "setSteps: "+"setting "+steps+" steps" );
        setChanged();
        notifyObservers();
    }

    public void setBattery(Battery battery) {
        mBattery = battery;
        Log.e(TAG, "setBattery: "+battery.toString() );
        setChanged();
        notifyObservers();
    }

    public void setLeParams(LeParams params) {
        mLeParams = params;
    }

}