package com.codepath.timeline.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
	private int space;

	public SpacesItemDecoration(int space) {
		this.space = space;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view,
							   RecyclerView parent, RecyclerView.State state) {
		outRect.bottom = space;
		outRect.top = 0;

		int position = parent.getChildLayoutPosition(view);
		// Add top margin only for the first item to avoid double space between items
		if (position == 0 || position == 1) {
			outRect.top = space;
		}
		if (position % 2 == 0) {
			outRect.left = space;
			outRect.right = space / 2;
		}
		else {
			outRect.left = space / 2;
			outRect.right = space;
		}
	}
}
