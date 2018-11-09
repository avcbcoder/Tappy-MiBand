package com.av.mainscreen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.av.mainscreen.R;
import com.github.zagum.switchicon.SwitchIconView;

/**
 * Created by Ankit on 10-11-2018.
 */

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingVH> {
    private static final String TAG = "SettingAdapter";
    Context ctx;

    int[] iconPack = {R.drawable.volume_up, R.drawable.volume_down, R.drawable.camera, R.drawable.play, R.drawable.forward, R.drawable.rewind, R.drawable.vibrate, R.drawable.timer};
    String[] title = {"Vol +", "Vol -", "Click", "Play", "Next", "Prev", "Vibrate", "Timer"};

    public SettingAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public SettingVH onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder: ");
        LayoutInflater lf = LayoutInflater.from(ctx);
        View v = lf.inflate(R.layout.zagum, parent, false);
        SettingVH VH = new SettingVH(v);
        return VH;
    }

    @Override
    public void onBindViewHolder(final SettingVH holder, int position) {
        Log.e(TAG, "onBindViewHolder: ");

    }

    @Override
    public int getItemCount() {
        return iconPack.length;
    }

    public class SettingVH extends RecyclerView.ViewHolder {

        public SettingVH(View itemView) {
            super(itemView);
        }
    }

}
