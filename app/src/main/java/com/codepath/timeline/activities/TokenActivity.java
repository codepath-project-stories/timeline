package com.codepath.timeline.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.codepath.timeline.R;

public class TokenActivity extends Activity {
	ContactsCompletionView completionView;
	Person[] people;
	ArrayAdapter<Person> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		people = new Person[]{
				new Person("Marshall Weir", "marshall@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"),
				new Person("Margaret Smith", "margaret@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"),
				new Person("Max Jordan", "max@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"),
				new Person("Meg Peterson", "meg@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"),
				new Person("Amanda Johnson", "amanda@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"),
				new Person("Terry Anderson", "terry@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460")
		};

		adapter = new ArrayAdapter<Person>(this, android.R.layout.simple_list_item_1, people);

		completionView = (ContactsCompletionView)findViewById(R.id.searchView);
		completionView.setAdapter(adapter);
	}

	// TokenActivity_REQUEST_CODE
	void onDone() {
		// send result back
		Intent data = new Intent();
		// data.putExtra(AppConstants.MOMENT_DESCRIPTION, etMomentTitle.getText().toString());
		// // TODO: v2 change to current location
		// data.putExtra(AppConstants.MOMENT_LOCATION, "San Francisco, CA");
		// data.putExtra(AppConstants.PHOTO_URI, takenPhotoUri.getPath());
		setResult(1, data);

		// closes the activity, pass data to parent
		finish();
	}
}
