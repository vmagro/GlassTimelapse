package com.socaldevs.timelapse.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;

import java.io.IOException;

public class MainActivity extends SherlockActivity implements ConnectionCallbacks, OnConnectionFailedListener, View.OnClickListener{
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private SharedPreferences sp;
    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    private String code = null;
    private final String TAG = "I HATE SHIT THAT DOESN'T WORK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getPreferences(MODE_PRIVATE);

        mPlusClient = new PlusClient.Builder(this, this, this)
                .setScopes(Constants.SCOPE_EMAIL,
                        Constants.SCOPE_LOGIN)
                .build();


        //Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent){
        Log.i(TAG, "In onActivityResult");
        if(requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK){
            mConnectionResult = null;
            mPlusClient.connect();
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
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
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
        mConnectionProgressDialog.dismiss();
        String user = mPlusClient.getAccountName();
        Log.i(TAG, "Connected: " + user);
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
                    connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
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
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    //This is how we connect to the server so we can get the code... Yumm... Code Stew...
    private AsyncTask<Void, Void, Void> connectToServer = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG, "Trying to connect to server");
            Bundle appActivities = new Bundle();
            //appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES,
            //        "<app-activity1> <app-activity2>");
            try {
                code = GoogleAuthUtil.getToken(
                        MainActivity.this,                // Context context
                        mPlusClient.getAccountName(),     // String accountName
                        Constants.SCOPES,                 // String scope
                        appActivities                     // Bundle bundle
                );

                Log.i(TAG, "Code: " + code);
                if(!code.equals(sp.getString(Constants.SP_CODE, "No Code"))){
                    Log.i(TAG, "Code given by server was different from last time");
                    sp.edit().putString(Constants.SP_CODE, code);
                }

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

            ItemScope target = new ItemScope.Builder()
                    .setId(Constants.ITEM_SCOPE_ID)
                    .setName(getResources().getString(R.string.app_name))
                    .setDescription("A brief time-lapse of this person's life.")
                    .setImage("http://socaldevs.com/wp-content/uploads/2011/10/SDLogo.png")
                    .build();

            Moment moment = new Moment.Builder()
                    .setType("http://schemas.google.com/ListenActivity")
                    .setTarget(target)
                    .build();

            return null;
        }
    };
}
