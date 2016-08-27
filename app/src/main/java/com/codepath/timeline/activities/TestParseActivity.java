package com.codepath.timeline.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.timeline.R;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestParseActivity extends AppCompatActivity {

	@BindView(R.id.getMockStoryList)
	Button getMockStoryList;
	@BindView(R.id.getStoryList)
	Button getStoryList;
	@BindView(R.id.getStoryList2)
	Button getStoryList2;
	@BindView(R.id.output)
	TextView output;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_parse);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.getMockStoryList)
	void generateStories() {
		List<Story> storyList = TimelineClient.getInstance().getMockStoryList(this);
		Story.saveToParse(storyList);
		output.setText("getMockStoryList");
	}

	@OnClick(R.id.getStoryList)
	void getStoryList() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		TimelineClient.getInstance().getStoryList(
				currentUser,
				// set up callback
				new TimelineClient.TimelineClientGetStoryListener() {
					@Override
					public void onGetStoryList(List<Story> itemList) {
						String buffer = "getStoryList\n";
						if (itemList != null) {
							for (Story eachStory : itemList) {
								buffer = buffer + eachStory.getTitle() + "\n";
							}
						}
						output.setText(buffer);
					}
				});
	}

	@OnClick(R.id.getStoryList2)
	void getStoryList2() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		TimelineClient.getInstance().getStoryList2(
				currentUser,
				// set up callback
				new TimelineClient.TimelineClientGetStoryListener() {
					@Override
					public void onGetStoryList(List<Story> itemList) {
						String buffer = "getStoryList2\n";
						if (itemList != null) {
							for (Story eachStory : itemList) {
								buffer = buffer + eachStory.getTitle() + "\n";
							}
						}
						output.setText(buffer);
					}
				});
	}
}
