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

    private static final String TWITTER_KEY = "j2GdKjcnqaIGlSwf8FYTUnMLM";
    private static final String TWITTER_SECRET = "DruaY9IZWepkWZt8s7quFH9twwFE7Nzpuq8txzwHEYw9NMyUhU";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        Parse.initialize(this, "McMcH2r1mHPLVayThixzxqiVQUesUOUYIoDLXVIp", "5T8YLuCG3uU8p98BteGHRnysKT0Z2Hd1NlTPRhsa");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseTwitterUtils.initialize("j2GdKjcnqaIGlSwf8FYTUnMLM", "DruaY9IZWepkWZt8s7quFH9twwFE7Nzpuq8txzwHEYw9NMyUhU");
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
