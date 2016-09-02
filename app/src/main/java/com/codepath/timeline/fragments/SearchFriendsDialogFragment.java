package com.codepath.timeline.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.codepath.timeline.R;
import com.codepath.timeline.adapters.MultiAutoCompleteTextViewArrayAdapter;
import com.codepath.timeline.network.TimelineClient;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SearchFriendsDialogFragment extends DialogFragment {

    // TODO: do we need this?
    private Unbinder unbinder;

    @BindView(R.id.multiAutoCompleteTextView)
    MultiAutoCompleteTextView multiAutoCompleteTextView;
    @BindView(R.id.button)
    Button button;

    List<ParseUser> mUsers;
    List<ParseUser> mUsersSelected;
    ArrayAdapter adapter;

    public SearchFriendsDialogFragment() {
    }

    public static SearchFriendsDialogFragment newInstance(String title) {
        SearchFriendsDialogFragment frag = new SearchFriendsDialogFragment();
        return frag;
    }

    // Defines the listener interface with a method passing back data result
    public interface SearchDialogListener {
        void onFinishSearchDialog(List<ParseUser> collabs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            // Todo: pass user id to fragment to retrieve friends list
        }
        View view = inflater.inflate(R.layout.fragment_search_friends, container, false);
        unbinder = ButterKnife.bind(this, view);

        mUsers = null;
        adapter = null;

        // Todo: pass user id to fragment to retrieve friends list
        TimelineClient.getInstance().getFriendsList(
                null,
                new TimelineClient.TimelineClientGetFriendListListener() {
                    @Override
                    public void onGetFriendList(List<ParseUser> itemList) {
                        mUsers = itemList;
                        adapter = new MultiAutoCompleteTextViewArrayAdapter(
                                getContext(),
                                // TODO: use custom layout
                                android.R.layout.simple_list_item_1,
                                mUsers);
                        multiAutoCompleteTextView.setAdapter(adapter);
                        multiAutoCompleteTextView.setThreshold(1);
                        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                    }
                });

        // TODO: or setOnItemSelectedListener()
        // TODO: how if users clean up multiAutoCompleteTextView after selecting some items
        mUsersSelected = new ArrayList<>();
        multiAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mUsersSelected.add((ParseUser) adapter.getItem(position));
            }
        });

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
                SearchFriendsDialogFragment.this.dismiss();
            }
        });
    }

    // users can click somewhere out of this DialogFragment to close this DialogFragment
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        notifyOnFinishSearchDialog();
        unbinder.unbind();
    }

    void notifyOnFinishSearchDialog() {
        SearchDialogListener listener =
                (SearchFriendsDialogFragment.SearchDialogListener) getActivity();
        if (listener != null) {
            listener.onFinishSearchDialog(mUsersSelected);
        }
    }

}
