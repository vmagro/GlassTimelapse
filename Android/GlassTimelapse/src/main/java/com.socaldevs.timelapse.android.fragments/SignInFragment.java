package com.socaldevs.timelapse.android.fragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.plus.PlusClient;
import com.socaldevs.timelapse.android.Constants;
import com.socaldevs.timelapse.android.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by vincente on 9/7/13.
 */
public class SignInFragment extends SherlockFragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, View.OnClickListener{

    private PlusClient mPlusClient;
    private ProgressDialog mConnectionProgressDialog;
    private SharedPreferences sp;
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
    private final String TAG = "I'm in the freaking fragment";
    private ConnectionResult mConnectionResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        Log.i(TAG, "In SignInFragment");
        sp = getSherlockActivity().getPreferences(getActivity().MODE_PRIVATE);
        mPlusClient = new PlusClient.Builder(getSherlockActivity(), this, this)
                .setScopes(Constants.SCOPE_EMAIL,
                        Constants.SCOPE_LOGIN)
                .build();

        //Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(getSherlockActivity());
        mConnectionProgressDialog.setMessage("Signing in...");
        rootView.findViewById(R.id.sign_in_button).setOnClickListener(this);

        //If we already have an access code, lets authorize without their permission
        if(sp.contains(Constants.SP_CODE)){
            mConnectionProgressDialog.show();
            mPlusClient.connect();
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent){
        Log.i(TAG, "In onActivityResult");
        if(requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == SherlockFragmentActivity.RESULT_OK){
            mConnectionResult = null;
            mPlusClient.connect();
        }
        else if(requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == SherlockFragmentActivity.RESULT_CANCELED){
            if(mConnectionProgressDialog.isShowing())
                mConnectionProgressDialog.dismiss();
        }
    }

    //Handling all of the OnClick Stuff
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()){
            if(mConnectionResult == null){
                mConnectionProgressDialog.show();
                mPlusClient.connect();
            } else {
                try{
                    mConnectionResult.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLVE_ERR);
                }catch (IntentSender.SendIntentException e){
                    //Try connecting again.
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
            }
        }
    }

    //Is called when the PlusClient Connects.
    //Once we connect, we will get the code from the server... Since we know where it is now.
    @Override
    public void onConnected(Bundle bundle) {
        //We've resolved any connection errors.
        String user = mPlusClient.getAccountName();
        Log.i(TAG, "Connected: " + user + " with userID: " + mPlusClient.getCurrentPerson().getId());

        //Save the UserID so we can get it for vinnie's glass crap
        sp.edit().putString(Constants.SP_ID, mPlusClient.getCurrentPerson().getId()).commit();
        if(!connectToServer.getStatus().equals(AsyncTask.Status.RUNNING))
            connectToServer.execute();
    }

    //What do we do when the user disconnects our service? We Cry.
    @Override
    public void onDisconnected() {
        Log.i(TAG, "user disconnected");
    }

    //The User Failed to connect. WTF. If we found a resolution to his/her stupid problem,
    //We will try to fix it, else, we are screwed and abandon ship. We will save the booty just in
    //case if we try to connect again.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(mConnectionProgressDialog.isShowing()){
            //The user clicked the sign-in button already. Start to resolve connection errors.
            //Wait until the onConnected() to dismiss the connection dialog.
            if(connectionResult.hasResolution()){
                try{
                    Log.i(TAG, "Sending for result");
                    connectionResult.startResolutionForResult(getSherlockActivity(), REQUEST_CODE_RESOLVE_ERR);
                }catch (IntentSender.SendIntentException e){
                    mPlusClient.connect();
                }
            }
        }

        //Save the intent so that we can start an activity when the user clicks the sign-in button
        mConnectionResult = connectionResult;
    }

    //STOP! HAMMER TIME!!
    @Override
    public void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    private String code;
    //This is how we connect to the server so we can get the code... Yumm... Code Stew...
    private AsyncTask<String, Void, Void> connectToServer = new AsyncTask<String, Void, Void>() {
        @Override
        protected Void doInBackground(String... voids) {
            Log.i(TAG, "Trying to connect to server");
            Bundle appActivities = new Bundle();
            //appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES,
            //        "<app-activity1> <app-activity2>");
            try {

                code = GoogleAuthUtil.getToken(
                        getSherlockActivity(),                // Context context
                        mPlusClient.getAccountName(),     // String accountName
                        Constants.SCOPES,                 // String scope
                        appActivities                     // Bundle bundle
                );

                Log.i(TAG, "Code: " + code);
                if(!code.equals(sp.getString(Constants.SP_CODE, ""))){
                    Log.i(TAG, "Code given by server was different from last time");
                    sp.edit().putString(Constants.SP_CODE, code).commit();
                }
                //Start Vinnie's OAUTH Stuff;
                new AcceptThread().start();
            } catch (IOException transientEx) {
                // network or server error, the call is expected to succeed if you try again later.
                // Don't attempt to call again immediately - the request is likely to
                // fail, you'll hit quotas or back-off.
                Log.e(TAG, "IOException");
                transientEx.printStackTrace();
            } catch (UserRecoverableAuthException userAuthEx) {
                //Lets Try this again. We need to reauthenticate because something went wrong and
                //we done fucked up
                startActivityForResult(userAuthEx.getIntent(), REQUEST_CODE_RESOLVE_ERR);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Void result) {
            mConnectionProgressDialog.dismiss();
            switchToNewsFeed();
            getSherlockActivity().sendBroadcast(new Intent(Constants.INTENT_UNLOCK_ID));
        }
    };

    private void switchToNewsFeed(){
        Log.i(TAG, "Switching to News Feed");
        if(!sp.getString(Constants.SP_CODE, "").equals(""))
            getSherlockActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_main, new NewsFeedFragment()).commit();
    }

    private static final UUID BT_UUID = UUID
            .fromString("dea9c2b5-7136-43fc-979d-a293af71018e");
    private static final String BT_SERVICE_NAME = "glasstimelapse";

    private class AcceptThread extends Thread {

        private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                tmp = adapter.listenUsingRfcommWithServiceRecord(BT_SERVICE_NAME, BT_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public void manageConnectedSocket(BluetoothSocket socket) {
        BufferedReader is;
        Log.i("status", "connected");
        try {
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String id = is.readLine();
            Log.d("glassId", id);

            JSONObject mJsonObject = new JSONObject();
            mJsonObject.put("glassId", id);
            mJsonObject.put("googleId", sp.getString(Constants.SP_ID, "null"));

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://hip-apricot-331.appspot.com/pairGlass");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("glassId", id));
            nameValuePairs.add(new BasicNameValuePair("googleId", sp.getString(Constants.SP_ID, "null")));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(httpPost);
            System.out.println(response.getEntity().getContent().toString());

        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
