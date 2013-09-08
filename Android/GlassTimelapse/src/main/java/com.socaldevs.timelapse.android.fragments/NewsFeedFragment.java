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
import com.socaldevs.timelapse.android.SyncServerPostcards;

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

        ArrayList<Postcard> arrayList = new ArrayList<Postcard>();
        arrayList.add(Constants.POSTCARD_LOADING);

        postcardAdapter = new PostcardAdapter(getActivity().getBaseContext(), R.layout.postcard_list_item,
                arrayList);

        newsFeed.setAdapter(postcardAdapter);
        getSherlockActivity().sendBroadcast(new Intent(Constants.INTENT_UNLOCK_ID));

        SyncServerPostcards syncAsync = new SyncServerPostcards(arrayList, postcardAdapter,
                Constants.SERVER_EVENTS + "?all=a");
        syncAsync.execute();
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}