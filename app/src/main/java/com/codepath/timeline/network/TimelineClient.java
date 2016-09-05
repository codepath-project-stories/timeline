package com.codepath.timeline.network;

import android.content.Context;
import android.util.Log;

import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.models.UserClient;
import com.codepath.timeline.util.ImageUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Network client for getting response from the server
 */
public class TimelineClient {
	private static final String TAG = "TimelineLog:" + TimelineClient.class.getSimpleName();

	private ParseQuery<Moment> mMomentDetailQuery;
	private ParseQuery<Story> mStoryListQuery;
	private ParseQuery<Story> mMomentListQuery;

	private static TimelineClient instance;

	private TimelineClient() {
	}

	public static TimelineClient getInstance() {
		if (instance == null) {
			instance = new TimelineClient();
		}
		return instance;
	}

	public List<Story> getMockStoryList(Context context) {
		JsonArray jsonArray = createMockJsonArray(context, "stories.json");
		if (jsonArray != null) {
			List<Story> storyList = Story.fromJsonArray(jsonArray);
			return storyList;
		} else {
			return null;
		}
	}

	// TODO: CACHE_ELSE_NETWORK
	// TODO: use a callback class instead of the following
	// other class can implement XXXListener
	// or, simply new TimelineClient.XXXListener() as callback

	public interface TimelineClientAddStoryListener {
		void onAddStoryList();
	}

	public interface TimelineClientGetFriendListListener {
		void onGetFriendList(List<ParseUser> itemList);
	}

	public interface TimelineClientGetStoryListener {
		void onGetStoryListSuccess(List<Story> itemList);
		void onGetStoryListFailed(String message);
	}

	public interface TimelineClientGetMomentListListener {
		void onGetMomentList(List<Moment> itemList);
		void onGetMomentChatList(List<Moment> itemList);
	}

	public interface TimelineClientGetUserListener {
		void onGetUser(ParseUser user);
	}

	public interface TimelineClientGetUserListListener {
		void onGetUserList(List<ParseUser> userList);
	}

	public interface TimelineClientGetCollaboratorListListener {
		void onGetCollaboratorList(List<ParseUser> itemList);
	}

	public interface TimelineClientGetMomentListener {
		void onGetMomentListener(Moment moment);
	}

	public interface TimelineClientUploadFileListener {
		void onUploadFileListener(ParseFile file);
	}

	public void addStoryList(final List<Story> storyList,
							 final TimelineClientAddStoryListener listener) {
		if (storyList.size() > 0) {
			Log.d("addStoryList", "storyList.size() > 0");
			ParseObject.saveAllInBackground(storyList, new SaveCallback() {
				@Override
				public void done(ParseException e) {
					Log.d("addStoryList", "saveAllInBackground done");
					if (e != null) {
						Log.d("addStoryList", e.toString());
					} else {
						ParseUser currentUser = UserClient.getCurrentUser();
						currentUser.addAll("stories", storyList);
						currentUser.saveInBackground(
								new SaveCallback() {
									@Override
									public void done(ParseException e) {
										Log.d("addStoryList", "saveInBackground done");
										if (e != null) {
											Log.d("addStoryList", e.toString());
										} else {
											if (listener != null) {
												listener.onAddStoryList(); // use callback
											}
										}
									}
								}
						);
					}
				}
			});
		}
	}

	// Used for populating mock data
	public void addMomentList(Story story, List<Moment> momentList) {
		if (momentList != null && momentList.size() > 0) {
			story.addAll("momentList", momentList);
			story.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e != null) {
						Log.e(TAG, "addMomentList exception: " + e.toString());
					} else {
						Log.d(TAG, "addMomentList successful");
					}
				}
			});
		}
	}

	// query User table
	public void getStoryList(ParseUser user,
							 final TimelineClientGetStoryListener listener) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		// http://parseplatform.github.io/docs/android/guide
		// fetchifneeded() could be an alternative to include()
		query.include("stories");
		// TODO: debug if this API can get 'owner' and 'collaboratorList' in "stories"
		query.getInBackground(
				user.getObjectId(),
				new GetCallback<ParseUser>() {
					@Override
					public void done(ParseUser user, ParseException e) {
						if (e == null) {
							if (user != null) {
								Log.d("getStoryList", user.getObjectId());
								if (listener != null) {
									listener.onGetStoryListSuccess(
											(ArrayList<Story>) user.get("stories")
									); // use callback
								}
							}
						} else {
							Log.d("getStoryList", "Error: " + e.getMessage());
						}
					}
				});
	}

	// DIANNE: Decided to use this API instead so I can include the 'owner' and 'collaboratorList'
	// for the story list in the LandingActivity
	public void getStoryList2(ParseUser user,
							  final TimelineClientGetStoryListener listener) {
		mStoryListQuery = ParseQuery.getQuery(Story.class);
		mStoryListQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		mStoryListQuery.whereEqualTo("owner", user);
		mStoryListQuery.include("owner"); // eagerly load the owner -- we need it for updating the story view
		mStoryListQuery.include("collaboratorList");
		mStoryListQuery.include("momentList");
		mStoryListQuery.findInBackground(new FindCallback<Story>() {
			@Override
			public void done(List<Story> itemList, ParseException e) {
				if (e != null || itemList == null) {
					String message = "Exception from getStoryList2: " + e.getMessage();
					onGetStoryListFailed(listener, message);
					return;
				}

				Log.d(TAG, "Success getStoryList2");
				if (listener != null) {
					listener.onGetStoryListSuccess(itemList); // use callback
				}
			}
		});
	}

	public void uploadFile(String fileName, String photoUri,
						   final TimelineClientUploadFileListener listener) {
		byte[] imageByte = ImageUtil.getImageData(photoUri);
		final ParseFile file = new ParseFile(fileName, imageByte);
		file.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception uploading file: " + e.getMessage());
					return;
				}

				Log.d(TAG, "Success uploadFile");
				if (listener != null) {
					listener.onUploadFileListener(file);
				}
			}
		});
	}

	// Query the DB for moments associated with this story
	public void getMomentList(String storyObjectId,
							  final TimelineClientGetMomentListListener listener) {
		mMomentListQuery = ParseQuery.getQuery(Story.class);
		mMomentListQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		mMomentListQuery.include("momentList");
		mMomentListQuery.include("momentList.author");
		mMomentListQuery.getInBackground(storyObjectId, new GetCallback<Story>() {
			@Override
			public void done(Story story, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from getMomentList: " + e.getMessage());
					return;
				}

				if (story != null) {
					Log.d(TAG, "Success getMomentList");
					if (listener != null) {
						listener.onGetMomentList(story.getMomentList());
					}
				}

				if (story != null) {
					Log.d(TAG, "Success getMomentChatList");
					if (listener != null) {
						listener.onGetMomentChatList(story.getMomentChatList());
					}
				}
			}
		});
	}

	public void getMoment(String momentObjectId,
						  final TimelineClientGetMomentListener listener) {
		ParseQuery<Moment> query = ParseQuery.getQuery(Moment.class);
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.include("author");
		query.getInBackground(momentObjectId, new GetCallback<Moment>() {
			@Override
			public void done(Moment moment, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from getMoment: " + e.getMessage());
					return;
				}

				if (moment != null) {
					Log.d(TAG, "Success getMoment: " + moment);
					if (listener != null) {
						listener.onGetMomentListener(moment);
					}
				}
			}
		});
	}

	// Includes moment details + commentList
	public void getMomentDetails(String momentObjectId,
								 final TimelineClientGetMomentListener listener) {
		mMomentDetailQuery = ParseQuery.getQuery(Moment.class);
		mMomentDetailQuery.include("author");
		mMomentDetailQuery.include("commentList");
		mMomentDetailQuery.include("commentList.author");
		mMomentDetailQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		mMomentDetailQuery.getInBackground(momentObjectId, new GetCallback<Moment>() {
			@Override
			public void done(Moment moment, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from getMomentDetails: " + e.getMessage());
					return;
				}

				if (moment != null) {
					Log.d(TAG, "Success getMomentDetails: " + moment);
					if (listener != null) {
						listener.onGetMomentListener(moment);
					}
				}
			}
		});
	}

	public void addComment(final Moment moment, final Comment comment) {
		comment.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from saving comment: " + e.getMessage());
					return;
				}

				Log.d(TAG, "Success addComment");
				// Update the moment after saving the comment
				moment.add("commentList", comment);
				moment.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e != null) {
							Log.e(TAG, "Exception from saving moment: " + e.getMessage());
							return;
						}

						// Clear cached result for the moment list
						if (mMomentDetailQuery.hasCachedResult()) {
							Log.d(TAG, "Clearing moment detail cache");
							mMomentDetailQuery.clearCachedResult();
						}
						Log.d(TAG, "Successfully saved moment");
					}
				});
			}
		});
	}

	public void addMoment(final Moment moment, final String storyObjectId) {
		moment.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from adding moment: " + e.getMessage());
					return;
				}

				Log.d(TAG, "Successfully added moment");
				Log.d(TAG, "Adding moment to story: " + storyObjectId);
				ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
				query.getInBackground(storyObjectId, new GetCallback<Story>() {
					@Override
					public void done(Story story, ParseException e) {
						if (e != null) {
							Log.e(TAG, "Exception from fetching story: " + e.getMessage());
							return;
						}

						Log.d(TAG, "Successfully fetched story");
						story.add("momentList", moment);
						story.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if (e != null) {
									Log.e(TAG, "Exception from saving moment: " + e.getMessage());
									return;
								}

								// Clear cached result for the moment list
								if (mMomentListQuery.hasCachedResult()) {
									Log.d(TAG, "Clearing moment list query");
									mMomentListQuery.clearCachedResult();
								}
								Log.d(TAG, "Successfully saved moment");
							}
						});
					}
				});
			}
		});
	}

	public void addStory(final Story story) {
		story.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from adding story: " + e.getMessage());
					return;
				}

				ParseUser user = UserClient.getCurrentUser();
				if (user != null) {
					user.add("stories", story);
					user.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							if (e != null) {
								Log.e(TAG, "Exception from saving user story: " + e.getMessage());
								return;
							}

							if (mStoryListQuery.hasCachedResult()) {
								Log.d(TAG, "Clearing user story cache");
								mStoryListQuery.clearCachedResult();
							}
							Log.d(TAG, "Successfully saved story");
						}
					});
				}
			}
		});
	}

	// query User table
	public void getCollaboratorList(Story story,
									final TimelineClientGetCollaboratorListListener listener) {
		ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
		// http://parseplatform.github.io/docs/android/guide
		// fetchifneeded() could be an alternative to include()
		query.include("collaboratorList");
		query.getInBackground(
				story.getObjectId(),
				new GetCallback<Story>() {
					@Override
					public void done(Story story, ParseException e) {
						if (e == null) {
							if (story != null) {
								Log.d("getCollaboratorList", story.getObjectId());
								if (listener != null) {
									listener.onGetCollaboratorList(
											(ArrayList<ParseUser>) story.get("collaboratorList")
									); // use callback
								}
							}
						} else {
							Log.d("getCollaboratorList", "Error: " + e.getMessage());
						}
					}
				});
	}

	public void getUserListByIds(ArrayList<String> objectIds,
								 final TimelineClientGetUserListListener listener) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereContainedIn("objectId", objectIds);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> userList, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from adding getUserListByIds: " + e.getMessage());
					return;
				}

				if (userList != null) {
					if (listener != null) {
						listener.onGetUserList(userList);
					}
				}
			}
		});
	}

	public void addFriendsList(ParseUser user, List<ParseUser> friendsList) {
		user.put("friendsList", friendsList);
		user.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from adding addFriendsList: " + e.getMessage());
					return;
				}

				Log.d(TAG, "Success addFriendsList");
			}
		});
	}

	public void getFriendsList(ParseUser user,
							   final TimelineClientGetFriendListListener listener) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.include("friendsList");
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from getFriendsList: " + e.getMessage());
					return;
				}

				if (user != null) {
					Log.d(TAG, "Success getFriendsList");
					List<ParseUser> friendsList = (List<ParseUser>) user.get("friendsList");
					if (listener != null && friendsList != null) {
						listener.onGetFriendList(friendsList);
					}
				}
			}
		});
	}

	public void getUser(final String userObjectId,
						final TimelineClientGetUserListener listener) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.getInBackground(userObjectId, new GetCallback<ParseUser>() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (e != null) {
					Log.e(TAG, "Exception from getUser(" + userObjectId + "): " + e.getMessage());
					return;
				}

				if (user != null) {
					Log.d(TAG, "Success getUser");
					if (listener != null) {
						listener.onGetUser(user);
					}
				}
			}
		});
	}

	public void getSharedStoryList(final ParseUser user,
								   final TimelineClientGetStoryListener listener) {
		ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
		query.whereEqualTo("collaboratorList", user);
		query.include("owner"); // eagerly load the owner -- we need it for updating the story view
		query.include("collaboratorList");
		query.include("momentList");
		// Execute the find asynchronously
		query.findInBackground(new FindCallback<Story>() {
			@Override
			public void done(List<Story> itemList, ParseException e) {
				if (e != null || itemList == null) {
					String message = "Exception from getSharedStoryList: " + e.getMessage();
					onGetStoryListFailed(listener, message);
					return;
				}

				Log.d("getSharedStoryList", Integer.toString(itemList.size()));
				List<Story> sharedStoryList = new ArrayList<Story>();

				// Remove stories that were created by the current user
				for (Story story : itemList) {
					if (!story.getOwner().equals(user)) {
						sharedStoryList.add(story);
					}
				}

				// Access the array of results here
				if (listener != null) {
					listener.onGetStoryListSuccess(sharedStoryList); // use callback
				}
			}
		});
	}


	private void onGetStoryListFailed(TimelineClientGetStoryListener listener, String message) {
		Log.e(TAG, message);
		if (listener != null) {
			listener.onGetStoryListFailed(message);
		}
	}


	// TEST: Create mock response
	protected JsonArray createMockJsonArray(Context context, String jsonFileName) {
		JsonArray jsonArray = null;

		try {
			InputStream is = context.getAssets().open(jsonFileName);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();

			String mockResponse = new String(buffer, "UTF-8");
			JsonParser jsonParser = new JsonParser();
			jsonArray = (JsonArray) jsonParser.parse(mockResponse);
		} catch (IOException ex) {
			ex.printStackTrace();
			jsonArray = null;
		}

		return jsonArray;
	}
}
