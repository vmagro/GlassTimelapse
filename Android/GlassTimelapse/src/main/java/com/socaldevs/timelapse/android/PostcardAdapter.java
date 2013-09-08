package com.socaldevs.timelapse.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
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
            holder.imgView          = (ImageView) row.findViewById(R.id.postcard_image);
            holder.userView         = (TextView) row.findViewById(R.id.postcard_user);
            holder.locationView     = (TextView) row.findViewById(R.id.postcard_location);
            holder.downloadImageSet = false;

            if(Build.VERSION.SDK_INT < 16)
                row.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.big_card));
            else
                row.setBackground(context.getResources().getDrawable(R.drawable.big_card));
            holder.imgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_launcher));

            row.setTag(holder);
        }
        else{
            holder = (PostcardHolder)row.getTag();
        }

        Postcard postcard   = postcards.get(position);
        holder.videoUrl     = postcard.getVideoUrl();
        holder.previewUrl   = postcard.getPreviewUrl();

        holder.userView.setText(postcard.getUser());
        holder.locationView.setText(postcard.getLocation());

        //If we havn't downloaded the previous preview, lets download it.
        if(!holder.downloadImageSet){
            loadImage(holder.imgView, holder.previewUrl);
            holder.downloadImageSet = true;
        }

        return row;
    }

    @Override
    public int getCount(){
        return postcards.size();
    }
    static class PostcardHolder{
        ImageView imgView;
        TextView userView, locationView;
        String videoUrl, previewUrl;
        boolean downloadImageSet;
    }

    private void loadImage(ImageView iv, String url) {
        DownloadTask asyncTask = new DownloadTask();
        asyncTask.execute(iv, url);
    }

    class DownloadTask extends AsyncTask<Object,Object, Object> {
        private ImageView iv;
        private InputStream is = null;
        private Bitmap imageDrawable = null;
        @Override
        protected Object doInBackground(Object... params) {
            iv = (ImageView) params[0];

            try {
                is  = new DefaultHttpClient().execute(new HttpPost(params[1].toString())).getEntity().getContent();
                imageDrawable = BitmapFactory.decodeStream(is);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return imageDrawable;
        }
        @Override
        protected void onPostExecute(Object response) {
            super.onPostExecute(response);
            if(response != null && iv != null){
                iv.setImageBitmap((Bitmap) response);
            }
        }
    }
}
