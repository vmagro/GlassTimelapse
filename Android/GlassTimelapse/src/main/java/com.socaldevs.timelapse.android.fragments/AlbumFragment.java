package com.socaldevs.timelapse.android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.socaldevs.timelapse.android.Constants;
import com.socaldevs.timelapse.android.Postcard;
import com.socaldevs.timelapse.android.PostcardAdapter;
import com.socaldevs.timelapse.android.R;

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
public class AlbumFragment extends SherlockFragment{

    private ListView postcardListView;
    private SharedPreferences sp;
    private ArrayList<Postcard> postcardArrayList;
    private PostcardAdapter adapter;

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
        return rootView;
    }


}