package com.codepath.timeline.activities;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.tokenautocomplete.TokenCompleteTextView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ContactsCompletionView extends TokenCompleteTextView<Person> {
    private Context context;
    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected View getViewForObject(Person person) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        RelativeLayout view = (RelativeLayout) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);

        ImageView image = (ImageView) view.findViewById(R.id.image);

        // Glide.with(context).load(R.drawable.bg_messages).into(image);
        Glide.with(context).load(person.getImage())
                .fitCenter()
                .bitmapTransform(new CropCircleTransformation(context))
                .placeholder(R.drawable.debug_profile)
                .error(R.drawable.debug_profile)
                .into(image);

        TextView text = (TextView) view.findViewById(R.id.name);
        text.setText(person.getName());

        return view;
    }

    @Override
    protected Person defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');
        if (index == -1) {
            return new Person(
                    completionText,
                    completionText.replace(" ", "") + "@example.com",
                    "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"
            );
        } else {
            return new Person(
                    completionText.substring(0, index),
                    completionText,
                    "https://avatars3.githubusercontent.com/u/2633155?v=3&s=460"
            );
        }
    }
}
