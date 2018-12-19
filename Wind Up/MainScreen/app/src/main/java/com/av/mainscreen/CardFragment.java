package com.av.mainscreen;

/**
 * Created by Ankit on 02-11-2018.
 */

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import static android.content.ContentValues.TAG;


public class CardFragment extends Fragment {

    public static String KEY_POS = "pos";
    private CardView mCardView;

    public static CardFragment newInstance(int pos) {
        Bundle args = new Bundle();
        args.putInt(KEY_POS, pos);
        CardFragment fragment = new CardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Find position of this fragment
        int pos = getArguments().getInt(KEY_POS);

        // Inflate this fragment
        View view = inflater.inflate(R.layout.card_tap, container, false);

        // set elevation of this card view to modify looks
        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);

        // Find properties(widgets) of this fragment
        TextView title = view.findViewById(R.id.card_tap_title);

        RecyclerView recyclerView=view.findViewById(R.id.card_tap_recyclerView);
        recyclerView.setAdapter(new IconAdapter(getContext(),pos));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.addItemDecoration(new SpacesItemDecoration(10));

        // set properties accordingly
        switch (pos) {
            case 0:
                title.setText("Single Tap");
                break;
            case 1:
                title.setText("Double Tap");
                break;
            case 2:
                title.setText("Tripple Tap");
                break;
            default:
                title.setText("Unknown Tap");
                break;
        }
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}