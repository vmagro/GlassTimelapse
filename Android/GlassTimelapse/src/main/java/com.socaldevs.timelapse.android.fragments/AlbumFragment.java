package com.socaldevs.timelapse.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.socaldevs.timelapse.android.Postcard;
import com.socaldevs.timelapse.android.PostcardAdapter;
import com.socaldevs.timelapse.android.R;

import java.util.ArrayList;

/**
 * Created by vincente on 9/7/13.
 */
public class AlbumFragment extends SherlockFragment{

    private ListView postcardListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        getSherlockActivity().getSupportActionBar().setTitle("My Album");

        postcardListView = (ListView) rootView.findViewById(R.id.albumListView);

        ArrayList<Postcard> postcardArrayList = new ArrayList<Postcard>();
        postcardArrayList.add(new Postcard("Loading Postcards", "This might take a moment",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png"));

        PostcardAdapter adapter = new PostcardAdapter(getActivity().getBaseContext(), R.id.albumListView,
                postcardArrayList);
        postcardListView.setAdapter(adapter);

        return rootView;
    }
}
