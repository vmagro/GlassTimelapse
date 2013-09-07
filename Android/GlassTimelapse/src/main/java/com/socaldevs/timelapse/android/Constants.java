package com.socaldevs.timelapse.android;

/**
 * Created by vincente on 9/7/13.
 */
public class Constants {
    public static final String ITEM_SCOPE_ID = "3309GlassHoles";
    public static final String SERVER_CLIENT_ID = "597615227690-pfgba7ficse1kf1su0qkgjllktcb7psf.apps.googleusercontent.com";
    public static final String SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
    public static final String SCOPE_LOGIN = com.google.android.gms.common.Scopes.PLUS_LOGIN;
    public static final String SCOPES = "oauth2:server:client_id:" + SERVER_CLIENT_ID + ":api_scope:" +
            SCOPE_LOGIN + " " + SCOPE_EMAIL;


    public static final String SP_CODE  = "wheresthecode";
    public static final String HAND_FRAG= "fragmentHandler";
}
