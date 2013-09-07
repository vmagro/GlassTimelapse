package com.socaldevs.timelapse.android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.socaldevs.timelapse.android.R;

/**
 * Created by vincente on 9/7/13.
 */
public class NewsFeedFragment extends SherlockFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control, container, false);
        Log.i("TAG", "In NewsFeed Fragment");
        return rootView;
    }
}