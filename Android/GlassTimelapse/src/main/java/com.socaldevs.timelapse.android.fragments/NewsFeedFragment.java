package com.socaldevs.timelapse.android.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.socaldevs.timelapse.android.Constants;
import com.socaldevs.timelapse.android.Postcard;
import com.socaldevs.timelapse.android.PostcardAdapter;
import com.socaldevs.timelapse.android.R;

import java.util.ArrayList;

/**
 * Created by vincente on 9/7/13.
 */
public class NewsFeedFragment extends SherlockFragment{

    private ListView newsFeed;
    private PostcardAdapter postcardAdapter;
    private final String TAG = "Finally got past OAUTH....";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        getSherlockActivity().sendBroadcast(new Intent(Constants.INTENT_UNLOCK_ID));

        newsFeed = (ListView) rootView.findViewById(R.id.newsFeedListView);

        ArrayList<Postcard> testPostcards = new ArrayList<Postcard>();
        testPostcards.add(new Postcard(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher), "Vincente Ciancio", "Huntington Beach", "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312005752631.png"));
        testPostcards.add(new Postcard(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher), "Vincente Ciancio", "Huntington Beach", "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312005752631.png"));
        testPostcards.add(new Postcard(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher), "Vincente Ciancio", "Huntington Beach", "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312005752631.png"));
        testPostcards.add(new Postcard(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher), "Vincente Ciancio", "Huntington Beach", "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312005752631.png"));
        testPostcards.add(new Postcard(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher), "Vincente Ciancio", "Huntington Beach", "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312005752631.png"));

        postcardAdapter = new PostcardAdapter(getActivity().getBaseContext(), R.layout.postcard_list_item,
                testPostcards);

        newsFeed.setAdapter(postcardAdapter);
        return rootView;
    }
}