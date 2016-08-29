package com.codepath.timeline.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.util.MockResponseGenerator;
import com.codepath.timeline.util.ParseApplication;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestParseActivity extends AppCompatActivity {

	@BindView(R.id.output)
	TextView output;
	@BindView(R.id.getMockStoryList)
	Button getMockStoryList;
	@BindView(R.id.getStoryList)
	Button getStoryList;
	@BindView(R.id.getStoryList2)
	Button getStoryList2;
	@BindView(R.id.getUserList)
	Button getUserList;
	@BindView(R.id.createMomentList)
	Button createMomentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_parse);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.getMockStoryList)
	void generateStories() {
		List<Story> storyList = TimelineClient.getInstance().getMockStoryList(this);
		if (ParseApplication.TURN_ON_PARSE) {
			TimelineClient.getInstance().addStoryList(storyList,
					new TimelineClient.TimelineClientAddStoryListener(){
						@Override
						public void onAddStoryList() {
							output.setText("getMockStoryList");
						}
					});
		}
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

	@OnClick(R.id.getUserList)
	void getUserList() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		TimelineClient.getInstance().getStoryList(
				currentUser,
				// set up callback
				new TimelineClient.TimelineClientGetStoryListener() {
					@Override
					public void onGetStoryList(List<Story> itemList) {
						if (itemList != null && itemList.size() > 0) {
							TimelineClient.getInstance().getUserList(
									itemList.get(0),
									new TimelineClient.TimelineClientGetUserListener() {
										@Override
										public void onGetUserList(List<ParseUser> itemList) {
											String buffer = "getUserList\n";
											if (itemList != null) {
												for (ParseUser eachUser : itemList) {
													buffer = buffer + eachUser.getEmail() + "\n";
												}
											}
											output.setText(buffer);
										}
									}
							);
						}
						else {
							String buffer = "getUserList\n";
							buffer = buffer + "empty stories" + "\n";
							output.setText(buffer);
						}
					}
				});
	}

	@OnClick(R.id.createMomentList)
	public void createMomentList() {
		Story story = new Story(
				"Dianne Story",
				"http://pbs.twimg.com/media/CpdUcQcWAAAwgwJ.jpg",
				"August 27, 2016"
		);
		story.setOwner(ParseUser.getCurrentUser());
		List<Moment> momentList = MockResponseGenerator.getInstance().getMomentList();
		TimelineClient.getInstance().addMomentList(story, momentList);
	}
}
