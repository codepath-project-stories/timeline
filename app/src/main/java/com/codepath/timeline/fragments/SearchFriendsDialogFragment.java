package com.codepath.timeline.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.codepath.timeline.R;
import com.codepath.timeline.models.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SearchFriendsDialogFragment extends DialogFragment {

    @BindView(R.id.btnAdd) Button btnAdd;
    private Unbinder unbinder;

    public SearchFriendsDialogFragment() {}

    public static SearchFriendsDialogFragment newInstance(String title) {
        SearchFriendsDialogFragment frag = new SearchFriendsDialogFragment();
        return frag;
    }

    // Defines the listener interface with a method passing back data result
    public interface SearchDialogListener {
        void onFinishSearchDialog(List<User> collabs);
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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchDialogListener listener = (SearchDialogListener) SearchFriendsDialogFragment.this.getTargetFragment();
                // Todo: add meaningful data (most likely a list of collaborators)
                List<User> collabs = new ArrayList<>();
//                listener.onFinishSearchDialog(collabs);
                SearchFriendsDialogFragment.this.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
