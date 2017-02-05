package net.dentare.akibamapandroid.adapter;

import android.content.Context;

import net.dentare.akibamapandroid.resources.Spot;

import java.util.List;

public class MyPostRecyclerViewAdapter extends BaseSpotListRecyclerViewAdapter{

    public MyPostRecyclerViewAdapter(Context context, List<Spot> spotList) {
        super(context, spotList);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        isVisibleRankText(holder,false);
    }
}