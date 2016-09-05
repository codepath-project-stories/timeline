package com.codepath.timeline.fragments;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.view.ContactsCompletionView;
import com.codepath.timeline.models.Person;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.models.UserClient;
import com.parse.ParseUser;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


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
        people.add(new Person("Marina Demo", "Marina@Demo", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Dianne Demo", "Dianne@Demo", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Chingyao Demo", "Chingyao@Demo", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Hugo Demo", "Hugo@Demo", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Demo Demo", "DemoDemo@Demo", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));
        people.add(new Person("Demo", "Demo@Demo", "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"));

        adapter = new FilteredArrayAdapter<Person>(context, R.layout.person_layout, people) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }

                Person p = getItem(position);
                Glide.with(context).load(p.getImage())
                        .fitCenter()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .placeholder(R.drawable.debug_profile)
                        .error(R.drawable.debug_profile)
                        .into((ImageView)convertView.findViewById(R.id.image));
                ((TextView)convertView.findViewById(R.id.name)).setText(p.getName());
                ((TextView)convertView.findViewById(R.id.email)).setText(p.getEmail());

                return convertView;
            }

            @Override
            protected boolean keepObject(Person person, String mask) {
                mask = mask.toLowerCase();
                return person.getName().toLowerCase().startsWith(mask) || person.getEmail().toLowerCase().startsWith(mask);
            }
        };

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
            // int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    // TODO: this doesn't work
    public void showSoftKeyboard(){
        if(view.requestFocus()){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
            // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    // TODO: make sure it is also working
    public void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
