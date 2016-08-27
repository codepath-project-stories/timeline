package com.codepath.timeline.util;

import android.app.Application;
import android.util.Log;

import com.codepath.timeline.models.CommentParse;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.models.TagParse;
import com.codepath.timeline.models.UserParse;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.interceptors.ParseLogInterceptor;

public class ParseApplication extends Application {
	public static boolean TURN_ON_PARSE = true;
	public static boolean DEMO_MODE = true;
	public static boolean TEST_PARSE = false;
	@Override
	public void onCreate() {
		super.onCreate();

		// Register your parse models
		ParseObject.registerSubclass(UserParse.class);
		ParseObject.registerSubclass(CommentParse.class);
		ParseObject.registerSubclass(TagParse.class);
		ParseObject.registerSubclass(Story.class);

		// set applicationId, and server server based on the values in the Heroku settings.
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
		// ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
	}

	// This is only used for debug purpose
	void test() {
		// New test creation of object below
		ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();

		UserParse theUser = new UserParse();
		theUser.setBody("Do laundry2");
		// Set the current user, assuming a user is signed in
		// theUser.setOwner(ParseUser.getCurrentUser());

		// Create the commentParse
		CommentParse commentParse = new CommentParse();
		commentParse.setBody("Get milk and eggs2");
		commentParse.setPost(theUser);
		// commentParse.setOwner(currentUser);
		commentParse.saveInBackground();

		TagParse tagParse = new TagParse();
		tagParse.setBody("This is a tag2");
		theUser.addTag(tagParse);

		// Immediately save the data asynchronously
		theUser.saveInBackground();
		// or for a more robust offline save
		// store the update on the device and push to the server once internet access is available.
		// theUser.saveEventually();

		// Create the post
		ParseObject myPost = new ParseObject("PostPost");
		myPost.put("title", "I'm Hungry");
		myPost.put("zz", "zzz");
		myPost.put("content", "Where should we go for lunch?");

		// Create the commentParse
		ParseObject myComment = new ParseObject("CommentComment");
		myComment.put("content", "Let's do Sushirrito.");

		// Add a relation between the Post and CommentParse
		myComment.put("parent", myPost);

		// This will save both myPost and myComment
		myComment.saveInBackground();

		// let’s say we have a few objects representing Author objects
		final ParseObject authorOne = new ParseObject("Author");
		authorOne.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null)
					Log.d("authorOne", e.toString());
				Log.d("authorOne", "zzz");

				// now we create a book object
				ParseObject book = new ParseObject("Book");

				// now let’s associate the authors with the book
				// remember, we created a "authors" relation on Book
				ParseRelation<ParseObject> relation = book.getRelation("authors");
				relation.add(authorOne);
				// stick the objects in an array

				// ArrayList<ParseObject> authors = new ArrayList<ParseObject>();
				// authors.add(authorOne);
				// authors.add(authorTwo);
				// authors.add(authorThree);
				// book.put("authors", authors);

				// now save the book object
				book.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e != null)
							Log.d("book", e.toString());
					}
				});
			}
		});
	}
}
