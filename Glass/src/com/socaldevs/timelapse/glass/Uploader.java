package com.socaldevs.timelapse.glass;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

public class Uploader extends AsyncTask<byte[], Void, Void> {

	private int index = 0;
	private int eventId = 0;
	private Context ctx;

	public Uploader(Context ctx, int eventId, int index) {
		this.ctx = ctx;
		this.eventId = eventId;
		this.index = index;
	}

	@Override
	protected Void doInBackground(byte[]... params) {
		String id = Secure.getString(ctx.getContentResolver(),
				Secure.ANDROID_ID);
		
		try {
			HttpClient cli = new DefaultHttpClient();
			
			HttpGet get = new HttpGet(Constants.UPLOAD_URL);
			BufferedReader reader = new BufferedReader(new InputStreamReader(cli.execute(get).getEntity().getContent()));
			String upload = reader.readLine();
			Log.i("upload url", upload);
			
			HttpPost post = new HttpPost(upload);
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		    reqEntity.addPart("glassId", new StringBody(id));
		    reqEntity.addPart("eventId",new StringBody(""+eventId));
		    reqEntity.addPart("index", new StringBody(""+index));
		    try{
		        ByteArrayBody bab = new ByteArrayBody(params[0], "image/jpeg", "image");
		        reqEntity.addPart("image", bab);
		    }
		    catch(Exception e){
		        //Log.v("Exception in Image", ""+e);
		        reqEntity.addPart("image", new StringBody(""));
		    }
		    post.setEntity(reqEntity);   
			
			cli.execute(post);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
