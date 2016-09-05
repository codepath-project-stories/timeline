package com.codepath.timeline.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.codepath.timeline.R;
import com.codepath.timeline.activities.ContactsCompletionView;
import com.codepath.timeline.activities.Person;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.network.UserClient;
import com.parse.ParseUser;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SearchFriendsDialogFragmentToken extends DialogFragment
        implements TokenCompleteTextView.TokenListener<Person>  {

    // TODO: do we need this?
    private Unbinder unbinder;

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.searchView)
    ContactsCompletionView completionView;

    Context context;
    List<Person> peopleSelected;
    List<Person> people;
    ArrayAdapter<Person> adapter;
    View view;

    public SearchFriendsDialogFragmentToken() {
    }

    public static SearchFriendsDialogFragmentToken newInstance(Context context, String title) {
        SearchFriendsDialogFragmentToken frag = new SearchFriendsDialogFragmentToken();
        frag.context = context;
        return frag;
    }

    @Override
    public void onTokenAdded(Person token) {
        peopleSelected.add(token);
    }

    @Override
    public void onTokenRemoved(Person token) {
        peopleSelected.remove(token);
    }

    // Defines the listener interface with a method passing back data result
    public interface SearchDialogListenerToken {
        void onFinishSearchDialogToken(List<Person> collabs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            // Todo: pass user id to fragment to retrieve friends list
        }

        // R.layout.fragment_search_friends
        view = inflater.inflate(R.layout.activity_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        peopleSelected = new ArrayList<>();
        people = new ArrayList<>();
        people.add(new Person("Marshall Weir", "marshall@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Margaret Smith", "margaret@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Max Jordan", "max@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Meg Peterson", "meg@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Amanda Johnson", "amanda@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Terry Anderson", "terry@example.com", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));

        adapter = new ArrayAdapter<Person>(context, android.R.layout.simple_list_item_1, people);

        completionView.allowDuplicates(false);
        completionView.setAdapter(adapter);
        completionView.setTokenListener(this);

        // Todo: pass user id to fragment to retrieve friends list
        TimelineClient.getInstance().getFriendsList(
                UserClient.getCurrentUser(),
                new TimelineClient.TimelineClientGetFriendListListener() {
                    @Override
                    public void onGetFriendList(List<ParseUser> itemList) {
                        for (ParseUser each : itemList) {
                            people.add(new Person(
                                    UserClient.getName(each),
                                    each.getEmail(),
                                    UserClient.getProfileImageUrl(each)
                            ));
                        }
                    }
                });

        showSoftKeyboard();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // users can click this button to close this DialogFragment
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyOnFinishSearchDialog();
                SearchFriendsDialogFragmentToken.this.dismiss();
            }
        });
    }

    // users can click somewhere out of this DialogFragment to close this DialogFragment
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notifyOnFinishSearchDialog();
        hideSoftKeyboard();
        unbinder.unbind();
    }

    void notifyOnFinishSearchDialog() {
        SearchDialogListenerToken listener =
                (SearchFriendsDialogFragmentToken.SearchDialogListenerToken) getActivity();
        if (listener != null) {
            listener.onFinishSearchDialogToken(peopleSelected);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void showSoftKeyboard(){
        if(view.requestFocus()){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
            // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
