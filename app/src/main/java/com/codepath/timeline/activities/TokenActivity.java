package com.codepath.timeline.activities;

import android.app.Activity;
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
				new Person("Marshall Weir", "marshall@example.com"),
				new Person("Margaret Smith", "margaret@example.com"),
				new Person("Max Jordan", "max@example.com"),
				new Person("Meg Peterson", "meg@example.com"),
				new Person("Amanda Johnson", "amanda@example.com"),
				new Person("Terry Anderson", "terry@example.com")
		};

		adapter = new ArrayAdapter<Person>(this, android.R.layout.simple_list_item_1, people);

		completionView = (ContactsCompletionView)findViewById(R.id.searchView);
		completionView.setAdapter(adapter);
	}
}
