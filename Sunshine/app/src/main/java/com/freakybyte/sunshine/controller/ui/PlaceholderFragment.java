package com.freakybyte.sunshine.controller.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freakybyte.sunshine.R;

/**
 * Created by Jose Torres in FreakyByte on 28/06/16.
 */
public class PlaceholderFragment extends Fragment {

    private View rootView;

    public PlaceholderFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }
}
