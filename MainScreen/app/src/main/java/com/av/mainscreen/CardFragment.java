package com.av.mainscreen;

/**
 * Created by Ankit on 02-11-2018.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;


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
        ToggleButton toggle = view.findViewById(R.id.card_tap_toogle);

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
}