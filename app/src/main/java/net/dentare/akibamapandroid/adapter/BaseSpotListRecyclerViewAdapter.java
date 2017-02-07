package net.dentare.akibamapandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.activity.DetailsActivity;
import net.dentare.akibamapandroid.resources.Spot;

import java.util.List;

public class BaseSpotListRecyclerViewAdapter extends RecyclerView.Adapter<BaseSpotListRecyclerViewAdapter.ViewHolder>{
    private final LayoutInflater mInflater;
    private final Context context;
    private final List<Spot> spotList;

    public BaseSpotListRecyclerViewAdapter(Context context, List<Spot> spotList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.spotList = spotList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.adapter_base_list_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Spot spot = spotList.get(position);
        long accessCount = spot.getAccess().getCount();
        holder.textViewTitle.setText(spot.getName());
        holder.textViewAccess.setText(context.getString(R.string.ranking_fragment_adapter_views,accessCount));
        holder.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id",spot.getId());
                context.startActivity(intent);
            }
        });
    }

    void setRankText(ViewHolder holder,int position){
        holder.textViewRank.setText(String.valueOf(getRank(getAccessCount(position))));
    }

    void isVisibleRankText(ViewHolder holder, boolean isVisible){
        holder.textViewRank.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private int getRank(long count){
        int rank = 1;
        for (Spot spot : spotList) {
            if (spot.getAccess().getCount() > count)
                rank++;
        }
        return rank;
    }

    private long getAccessCount(int position){
        Spot spot = spotList.get(position);
        return spot == null ? -1 : spot.getAccess().getCount();
    }

    @Override
    public int getItemCount() {
        return spotList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView nextButton;
        TextView textViewRank;
        TextView textViewTitle;
        TextView textViewAccess;
        ViewHolder(View itemView) {
            super(itemView);
            nextButton = (AppCompatImageView) itemView.findViewById(R.id.nextButton);
            textViewRank = (TextView) itemView.findViewById(R.id.textViewRank);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            textViewAccess = (TextView) itemView.findViewById(R.id.textViewAccess);
        }
    }
}