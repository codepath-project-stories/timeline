package com.codepath.timeline.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MultiAutoCompleteTextViewArrayAdapter extends ArrayAdapter<ParseUser> {
    private final Context mContext;
    private final List<ParseUser> musers;
    private final List<ParseUser> musers_All;
    private final List<ParseUser> musers_Suggestion;
    private final int mLayoutResourceId;

    public MultiAutoCompleteTextViewArrayAdapter(Context context, int resource, List<ParseUser> users) {
        super(context, resource, users);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.musers = new ArrayList<>(users);
        this.musers_All = new ArrayList<>(users);
        this.musers_Suggestion = new ArrayList<>();
    }

    public int getCount() {
        return musers.size();
    }

    public ParseUser getItem(int position) {
        return musers.get(position);
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
            ParseUser department = getItem(position);
            TextView name = (TextView) convertView.findViewById(android.R.id.text1);
            name.setText((String)department.get("name"));
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
                return (String)(((ParseUser) resultValue).get("name"));
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    musers_Suggestion.clear();
                    for (ParseUser department : musers_All) {
                        String name = (String) department.get("name");
                        if (name.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            musers_Suggestion.add(department);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = musers_Suggestion;
                    filterResults.count = musers_Suggestion.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                musers.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using musers.addAll((ArrayList<Department>) results.values);
                    List<?> result = (List<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof ParseUser) {
                            musers.add((ParseUser) object);
                        }
                    }
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    musers.addAll(musers_All);
                }
                notifyDataSetChanged();
            }
        };
    }
}
