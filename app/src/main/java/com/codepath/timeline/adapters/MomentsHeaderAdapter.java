package com.codepath.timeline.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.DateUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// Used to bind sticky header used in the timeline
public class MomentsHeaderAdapter extends MomentsAdapter implements StickyRecyclerHeadersAdapter<MomentsHeaderAdapter.MomentsHeaderViewHolder> {

  // Maps a year (i.e. what's going to be used as the header string) to the item's position in the list
  private HashMap<String, Integer> mYearHeaderMap = new HashMap<>();
  private int mCurrentYear = -1;

  public MomentsHeaderAdapter(Context context, List<Moment> mMomentList) {
    super(context, mMomentList);
  }

  /**
   * Get the ID of the header associated with this item.  For example, if your headers group
   * items by their first letter, you could return the character representation of the first letter.
   * Return a value &lt; 0 if the view should not have a header (like, a header view or footer view)
   *
   * @param position the position of the view to get the header ID of
   * @return the header ID
   */
  @Override
  public long getHeaderId(int position) {
    long headerPos = -1;
    Moment moment = mMomentList.get(position);
    if (moment != null) {
      String year = DateUtil.getYear(moment.getCreatedAtReal());
      if (mYearHeaderMap.get(year) != null) {
        // we already have it in the map, just grab the header position
        headerPos = mYearHeaderMap.get(year);
      } else {
        mYearHeaderMap.put(year, position);
        headerPos = position;
      }
    }

    return headerPos;
  }

  @Override
  public MomentsHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moment_header, parent, false);
    MomentsHeaderViewHolder viewHolder = new MomentsHeaderViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindHeaderViewHolder(MomentsHeaderViewHolder holder, int position) {
    Moment moment = mMomentList.get(position);
    if (moment != null && moment.getCreatedAtReal() != null) {
      String year = DateUtil.getYear(moment.getCreatedAtReal());
      holder.tvHeader.setText(year);
    }
  }


  public static class MomentsHeaderViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tvHeader)
    TextView tvHeader;

    public MomentsHeaderViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
