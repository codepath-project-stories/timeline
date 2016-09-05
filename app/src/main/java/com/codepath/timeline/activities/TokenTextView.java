package com.codepath.timeline.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.codepath.timeline.R;

public class TokenTextView extends TextView {

	public TokenTextView(Context context) {
		super(context);
	}

	public TokenTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		setCompoundDrawablesWithIntrinsicBounds(0, 0, selected ? R.drawable.close_x : 0, 0);
	}
}