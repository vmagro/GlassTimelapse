package com.socaldevs.timelapse.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
import com.socaldevs.timelapse.android.UpdateReceiver;

import java.util.ArrayList;

/**
 * Created by vincente on 9/7/13.
 */
public class NewsFeedFragment extends SherlockFragment implements AdapterView.OnItemClickListener{

    private ListView newsFeed;
    private PostcardAdapter postcardAdapter;
    private final String TAG = "Finally got past OAUTH....";
    ArrayList<Postcard> arrayList;
    private UpdateReceiver mUpdateReceiver;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        getSherlockActivity().sendBroadcast(new Intent(Constants.INTENT_UNLOCK_ID));
        getSherlockActivity().getSupportActionBar().setTitle("News Feed");
        newsFeed = (ListView) rootView.findViewById(R.id.newsFeedListView);
        newsFeed.setOnItemClickListener(this);

        arrayList = new ArrayList<Postcard>();
        arrayList.add(Constants.POSTCARD_LOADING);

        postcardAdapter = new PostcardAdapter(getActivity().getBaseContext(), R.layout.postcard_list_item,
                arrayList);

        newsFeed.setAdapter(postcardAdapter);
        getSherlockActivity().sendBroadcast(new Intent(Constants.INTENT_UNLOCK_ID));

        refreshCards();

        //Will receive an intent when we want to refresh
        mUpdateReceiver = new UpdateReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                arrayList.clear();
                arrayList.add(Constants.POSTCARD_REFRESHING);
                postcardAdapter.notifyDataSetChanged();
                refreshCards();
            }
        };
        getSherlockActivity().registerReceiver(mUpdateReceiver,
                new IntentFilter(Constants.INTENT_REFRESH));


        return rootView;
    }

    private void refreshCards(){
        SyncServerPostcards syncAsync = new SyncServerPostcards(arrayList, postcardAdapter,
                Constants.SERVER_EVENTS + "?all=a");
        syncAsync.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                arrayList.get(i).getVideoUrl()
        )));


    }

    @Override
    public void onPause() {
        super.onPause();
        getSherlockActivity().unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getSherlockActivity().registerReceiver(mUpdateReceiver, new IntentFilter(Constants.INTENT_REFRESH));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}