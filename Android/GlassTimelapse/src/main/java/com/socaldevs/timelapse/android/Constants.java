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

    public static final String SERVER_BASE      = "http://glass.ptzlabs.com";
    public static final String SERVER_EVENTS    = SERVER_BASE + "/events";
    public static final String SERVER_GLASS     = SERVER_BASE + "/glass";

    public static final String SP_CODE          = "wheresthecode";
    public static final String SP_ID            = "freakingid...";
    public static final String SP_NAME          = "person name....";

    public static final String INTENT_REFRESH   = "3308";
    public static final String INTENT_UNLOCK_ID = "3309";

    public static final Postcard POSTCARD_LOADING   = new Postcard("Loading Postcards",
            "This might take a moment",
            "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png",
            "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png");
    public static final Postcard POSTCARD_NONE      = new Postcard("No Postcards Available",
            "Try Again Later", "http://www.youtube.com/watch?v=jofNR_WkoCE",
            "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png");
    public static final Postcard POSTCARD_ERROR     = new Postcard("There Was An Error",
            "Try Again Later", "http://www.youtube.com/watch?v=jofNR_WkoCE",
            "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png");
    public static final Postcard POSTCARD_REFRESHING=new Postcard("Refreshing",
            "Hold on a Sec", "http://www.youtube.com/watch?v=jofNR_WkoCE",
            "http://socaldevs.com/wp-content/uploads/2011/07/socal-devs-2-big-e1312568698579.png");
}
