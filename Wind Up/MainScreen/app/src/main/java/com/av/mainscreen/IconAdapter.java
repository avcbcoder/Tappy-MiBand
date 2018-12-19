package com.av.mainscreen;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.av.mainscreen.constants.SETTINGS;
import com.av.mainscreen.database.SyncWithDB;
import com.github.zagum.switchicon.SwitchIconView;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Ankit on 03-11-2018.
 */

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconVH> {
    private static final String TAG = "IconAdapter";
    private Context ctx;
    private int pos;

    int[] iconPack = {R.drawable.volume_up, R.drawable.volume_down, R.drawable.camera, R.drawable.play, R.drawable.forward, R.drawable.rewind, R.drawable.vibrate, R.drawable.timer};
    String[] title = {"Vol +", "Vol -", "Click", "Play", "Next", "Prev", "Vibrate", "Timer"};
    HashMap<Integer, IconVH> holderHashMap = new HashMap<>();

    public IconAdapter(Context ctx, int pos) {
        this.ctx = ctx;
        this.pos = pos;
    }

    @Override
    public IconVH onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder: ");
        LayoutInflater lf = LayoutInflater.from(ctx);
        View v = lf.inflate(R.layout.zagum, parent, false);
        IconVH VH = new IconVH(v);
        return VH;
    }

    @Override
    public void onBindViewHolder(final IconVH holder, final int positionInRv) {
        Log.e(TAG, "onBindViewHolder: ");
        final int positionInArray = positionInRv % iconPack.length;
        holder.icon.setImageResource(iconPack[positionInArray]);
        holder.title.setText(title[positionInArray]);

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //holder.icon.setIconEnabled(!holder.icon.isIconEnabled());
                saveSetting(holder.icon, positionInArray);
            }
        });

        holderHashMap.put(positionInArray, holder);
    }

    private void saveSetting(SwitchIconView icon, int positionInArray) {
        boolean isEnabled = icon.isEnabled();
        switch (positionInArray) {
            case 0: // Increase Volume
                volumeSetting(1, isEnabled);
                break;
            case 1:// Decrease Volume
                volumeSetting(2, isEnabled);
                break;
            case 2: // Capture
                break;
            case 3:// play/pause
                musicSetting(3, isEnabled);
                break;
            case 4:// next
                musicSetting(1, isEnabled);
                break;
            case 5:// prev
                musicSetting(2, isEnabled);
                break;
            case 6:
                SETTINGS.taps[pos].VIBRATE = isEnabled ? 1 : 0;
                break;
            case 7:
                SETTINGS.taps[pos].TIMER = isEnabled ;
                break;
        }
        SyncWithDB.putSettingsInDB(ctx);
    }

    private void musicSetting(int state, boolean isEnabled) {
        int curr = !isEnabled ? 0 : state;
        SETTINGS.taps[pos].MUSIC = curr;
        holderHashMap.get(3).icon.setEnabled(curr == 3);
        holderHashMap.get(4).icon.setEnabled(curr == 1);
        holderHashMap.get(5).icon.setEnabled(curr == 2);
    }

    private void volumeSetting(int state, boolean isEnabled) {
        int curr = (isEnabled) ? state : 0; // what should be the state
        SETTINGS.taps[pos].VOL = curr;// update settings
        holderHashMap.get(0).icon.setEnabled(curr == 1);
        holderHashMap.get(1).icon.setEnabled(curr == 2);
    }

    @Override
    public int getItemCount() {
        return iconPack.length;
    }

    public class IconVH extends RecyclerView.ViewHolder {
        SwitchIconView icon;
        TextView title;

        public IconVH(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.zagum_icon);
            title = itemView.findViewById(R.id.zagum_text);
        }
    }

}
