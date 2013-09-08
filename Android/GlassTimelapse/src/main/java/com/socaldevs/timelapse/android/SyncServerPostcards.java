package com.socaldevs.timelapse.android;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vincente on 9/8/13.
 */
public class SyncServerPostcards extends AsyncTask<Void, Void, JSONArray>{

    ArrayList<Postcard> arrayList;
    PostcardAdapter adapter;
    String url;

    public SyncServerPostcards(ArrayList<Postcard> arrayList, PostcardAdapter adapter, String requestUrl){
        this.arrayList  = arrayList;
        this.adapter    = adapter;
        this.url        = requestUrl;
    }
    protected JSONArray doInBackground(Void... voids) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        JSONArray responseArray = null;
        try {
            response = httpClient.execute(httpGet);
            if(response != null){
                String rawResponse = EntityUtils.toString(response.getEntity());
                responseArray = new JSONArray(rawResponse);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return responseArray;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        arrayList.clear();
        for(int i=0; i<jsonArray.length(); i++){
            try {
                String user     = jsonArray.getJSONObject(i).getString("userId");
                String location = jsonArray.getJSONObject(i).getString("startTime");
                String videoUrl = "http://www.youtube.com/watch?v=jofNR_WkoCE";
                String preview  = "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-" +
                        "big-e1312568698579.png";

                Postcard temp = new Postcard(user, location, videoUrl, preview);
                arrayList.add(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }
}