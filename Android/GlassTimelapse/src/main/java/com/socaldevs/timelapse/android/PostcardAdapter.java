package com.socaldevs.timelapse.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by vincente on 9/7/13.
 */
public class PostcardAdapter extends ArrayAdapter<Postcard> {

    ArrayList<Postcard> postcards;
    Context context;

    public PostcardAdapter(Context context, int resource, ArrayList<Postcard> postcards) {
        super(context, resource);
        this.postcards = postcards;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PostcardHolder holder = null;
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.postcard_list_item, parent, false);

            holder = new PostcardHolder();
            holder.imgView      = (ImageView) row.findViewById(R.id.postcard_image);
            holder.userView     = (TextView) row.findViewById(R.id.postcard_user);
            holder.locationView = (TextView) row.findViewById(R.id.postcard_location);

            if(Build.VERSION.SDK_INT < 16)
                row.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.big_card));
            else
                row.setBackground(context.getResources().getDrawable(R.drawable.big_card));

            row.setTag(holder);
        }
        else{
            holder = (PostcardHolder)row.getTag();
        }

        Postcard postcard = postcards.get(position);
        holder.imgView.setImageBitmap(postcard.getPreview());
        holder.userView.setText(postcard.getUser());
        holder.locationView.setText(postcard.getLocation());

        return row;
    }

    @Override
    public int getCount(){
        return postcards.size();
    }
    static class PostcardHolder{
        ImageView imgView;
        TextView userView, locationView;
    }

    class DownloadTask extends AsyncTask<Object,Object, Object> {
        private ImageView iv;
        private InputStream is = null;
        private Drawable imageDrawable = null;
        @Override
        protected Object doInBackground(Object... params) {
            iv = (ImageView) params[0];

            try {
                is  = new DefaultHttpClient().execute(new HttpPost(params[1].toString())).getEntity().getContent();
                imageDrawable = Drawable.createFromStream((InputStream)is , "src name");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageDrawable;
        }
        @Override
        protected void onPostExecute(Object response) {
            super.onPostExecute(response);
            if(response != null){
                iv.setImageDrawable((Drawable)response);
            }

        }

    }

}
