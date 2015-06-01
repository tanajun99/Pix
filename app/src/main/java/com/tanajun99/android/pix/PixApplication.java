package com.tanajun99.android.pix;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class PixApplication extends Application {

    private static final String TWITTER_KEY = "";
    private static final String TWITTER_SECRET = "";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        Parse.initialize(this, "Key", "Key");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseTwitterUtils.initialize("TwitterKey", "TwitterKey");
        ParseFacebookUtils.initialize(this);
        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID_SEND, user.getObjectId());
        installation.saveInBackground();
    }
}
