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

import com.github.zagum.switchicon.SwitchIconView;

import java.util.Random;

/**
 * Created by Ankit on 03-11-2018.
 */

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconVH> {
    private static final String TAG = "IconAdapter";
    Context ctx;

    int[] iconPack = {R.drawable.volume_up, R.drawable.volume_down, R.drawable.camera, R.drawable.play, R.drawable.forward, R.drawable.rewind};
    String[] title = {"Vol +", "Vol -", "Click", "Play", "Next", "Prev"};

    public IconAdapter(Context ctx) {
        this.ctx = ctx;
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
    public void onBindViewHolder(final IconVH holder, int position) {
        Log.e(TAG, "onBindViewHolder: ");
        position = position % iconPack.length;
        holder.icon.setImageResource(iconPack[position]);
        holder.title.setText(title[position]);

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.icon.setIconEnabled(!holder.icon.isIconEnabled());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 6;
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
