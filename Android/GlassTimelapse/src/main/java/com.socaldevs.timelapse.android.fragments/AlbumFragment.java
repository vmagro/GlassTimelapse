package com.socaldevs.timelapse.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vincente on 9/7/13.
 */
public class AlbumFragment extends SherlockFragment implements AdapterView.OnItemClickListener{

    private ListView postcardListView;
    private SharedPreferences sp;
    private ArrayList<Postcard> postcardArrayList;
    private PostcardAdapter adapter;
    private UpdateReceiver mUpdateReceiver;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        getSherlockActivity().getSupportActionBar().setTitle("My Album");

        postcardListView = (ListView) rootView.findViewById(R.id.albumListView);
        sp = getSherlockActivity().getPreferences(Context.MODE_PRIVATE);

        postcardArrayList = new ArrayList<Postcard>();
        postcardArrayList.add(Constants.POSTCARD_LOADING);

         adapter = new PostcardAdapter(getActivity().getBaseContext(), R.id.albumListView,
                postcardArrayList);
        postcardListView.setAdapter(adapter);
        postcardListView.setOnItemClickListener(this);

        refreshCards();

        //Will receive an intent when we want to refresh
        mUpdateReceiver = new UpdateReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                postcardArrayList.clear();
                postcardArrayList.add(Constants.POSTCARD_REFRESHING);
                adapter.notifyDataSetChanged();
                refreshCards();
            }
        };
        getSherlockActivity().registerReceiver(mUpdateReceiver,
                new IntentFilter(Constants.INTENT_REFRESH));


        return rootView;
    }

    private void refreshCards(){
        SyncServerPostcards syncAsync = new SyncServerPostcards(postcardArrayList, adapter,
                Constants.SERVER_EVENTS + "?from="+sp.getString(Constants.SP_ID, "null"));
        syncAsync.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                postcardArrayList.get(i).getVideoUrl()
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