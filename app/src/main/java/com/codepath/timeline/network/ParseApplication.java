package com.codepath.timeline.network;

import android.app.Application;

import com.codepath.timeline.R;
import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.interceptors.ParseLogInterceptor;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class ParseApplication extends Application {
	// http://parseplatform.github.io/docs/android/guide

	public static boolean TURN_ON_PARSE = true;
	public static boolean DEMO_MODE = false;
	public static boolean TEST_PARSE = false;
	public static boolean TEST_SPOTIFY = false;
	public static boolean TEST_TokenAutoComplete = false;

	// TODO: use push notification instead of pulling and auto refresh every a few seconds
	public static int REFRESH_INTERVAL = 60000; // default to 60s to avoid server overhead
	// public static int REFRESH_INTERVAL = 5000; // default to 5s for the demo/production

	@Override
	public void onCreate() {
		super.onCreate();

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/HelveticaNeue-Regular.ttf")
				.setFontAttrId(R.attr.fontPath)
				.build()
		);

		// TODO: Local Store
		// http://guides.codepath.com/android/Building-Data-driven-Apps-with-Parse
		// Parse now supports a more powerful form of local data storage out of the box
		// which can be used to store and retrieve ParseObjects,
		// even when the network is unavailable.
		Parse.enableLocalDatastore(this);

		// Register your parse models
		ParseObject.registerSubclass(Comment.class);
		ParseObject.registerSubclass(Moment.class);
		ParseObject.registerSubclass(Story.class);

		// set applicationId, and server server based on the values in the Heroku settings_icon.
		// clientKey is not needed unless explicitly configured
		// any network interceptors must be added with the Configuration Builder given this syntax
		Parse.initialize(new Parse.Configuration.Builder(this)
				.applicationId("timeline-app") // should correspond to APP_ID env variable
				.clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
				.addNetworkInterceptor(new ParseLogInterceptor())
				.server("https://timeline-app.herokuapp.com/parse/").build());
	}

	public static void logout() {
		ParseUser.logOut();
		// ParseUser currentUser = UserClient.getCurrentUser(); // this will now be null
	}
}
