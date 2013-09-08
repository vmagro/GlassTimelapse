package com.socaldevs.timelapse.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class NewsFeedFragment extends SherlockFragment implements AdapterView.OnItemClickListener{

    private ListView newsFeed;
    private PostcardAdapter postcardAdapter;
    private final String TAG = "Finally got past OAUTH....";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        getSherlockActivity().sendBroadcast(new Intent(Constants.INTENT_UNLOCK_ID));
        getSherlockActivity().getSupportActionBar().setTitle("News Feed");
        newsFeed = (ListView) rootView.findViewById(R.id.newsFeedListView);
        newsFeed.setOnItemClickListener(this);

        ArrayList<Postcard> testPostcards = new ArrayList<Postcard>();
        testPostcards.add(new Postcard("Vincente Ciancio", "Huntington Beach",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png"));
        testPostcards.add(new Postcard("Vincente Ciancio", "Huntington Beach",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312005752631.png",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png"));
        testPostcards.add(new Postcard("Vincente Ciancio", "Huntington Beach",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png"));
        testPostcards.add(new Postcard("Vincente Ciancio", "Huntington Beach",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png"));
        testPostcards.add(new Postcard("Vincente Ciancio", "Huntington Beach",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png",
                "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png"));

        postcardAdapter = new PostcardAdapter(getActivity().getBaseContext(), R.layout.postcard_list_item,
                testPostcards);

        newsFeed.setAdapter(postcardAdapter);
        getSherlockActivity().sendBroadcast(new Intent(Constants.INTENT_UNLOCK_ID));
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}