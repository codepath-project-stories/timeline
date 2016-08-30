package com.codepath.timeline.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.codepath.timeline.network.UserClient;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MultiAutoCompleteTextViewArrayAdapter extends ArrayAdapter<ParseUser> {
    private final Context mContext;
    private final List<ParseUser> mUsers;
    private final List<ParseUser> mUsers_All;
    private final List<ParseUser> mUsers_Suggestion;
    private final int mLayoutResourceId;

    public MultiAutoCompleteTextViewArrayAdapter(Context context, int resource, List<ParseUser> users) {
        super(context, resource, users);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mUsers = new ArrayList<>(users);
        this.mUsers_All = new ArrayList<>(users);
        this.mUsers_Suggestion = new ArrayList<>();
    }

    public int getCount() {
        return mUsers.size();
    }

    public ParseUser getItem(int position) {
        return mUsers.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            ParseUser user = getItem(position);
            TextView name = (TextView) convertView.findViewById(android.R.id.text1);
            name.setText(UserClient.getName(user));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return UserClient.getName((ParseUser) resultValue);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    mUsers_Suggestion.clear();
                    for (ParseUser user : mUsers_All) {
                        String name = UserClient.getName(user);
                        if (name.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            mUsers_Suggestion.add(user);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mUsers_Suggestion;
                    filterResults.count = mUsers_Suggestion.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mUsers.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mUsers.addAll((ArrayList<user>) results.values);
                    List<?> result = (List<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof ParseUser) {
                            mUsers.add((ParseUser) object);
                        }
                    }
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    mUsers.addAll(mUsers_All);
                }
                notifyDataSetChanged();
            }
        };
    }
}
