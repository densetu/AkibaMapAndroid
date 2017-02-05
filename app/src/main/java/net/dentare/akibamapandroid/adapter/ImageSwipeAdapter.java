package net.dentare.akibamapandroid.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.fragment.ImageSwipeFragment.ImageSwipeListener;
import net.dentare.akibamapandroid.resources.SpotImage;
import net.dentare.akibamapandroid.util.Config;

import java.io.File;
import java.util.List;

public class ImageSwipeAdapter extends RecyclerView.Adapter<ImageSwipeAdapter.ViewHolder>{
    private final LayoutInflater mInflater;
    private final Context context;
    private final List<SpotImage> spotImages;
    private ImageSwipeListener listener;

    public ImageSwipeAdapter(Context context, List<SpotImage> spotImages, ImageSwipeListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.spotImages = spotImages;
        this.listener = listener;
    }

    public ImageSwipeAdapter(Context context, List<SpotImage> spotImages) {
        this(context,spotImages,null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.adapter_image_swipe, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position) {
        if (spotImages != null && spotImages.size() > position && spotImages.get(position) != null) {
            SpotImage spotImage = spotImages.get(position);
            String url = spotImage.getUrl();
            if (url.startsWith(Config.localImageSpacer)){
                url = url.replaceFirst(Config.localImageSpacer,"");
                File file = new File(url);
                if (file.exists())
                    Picasso.with(context).load(file).into(holder.imageViewMain);
            }else {
                if (url.startsWith(Config.firebaseImageSpacer))
                    url = url.replaceFirst(Config.firebaseImageSpacer,"");
                Picasso.with(context).load(url).into(holder.imageViewMain);
            }
        }

        final int p = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickItem(v, p);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (spotImages != null)
            return spotImages.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMain;
        TextView textViewDetails;
        public ViewHolder(View itemView) {
            super(itemView);
            imageViewMain = (ImageView) itemView.findViewById(R.id.imageViewMain);
            textViewDetails = (TextView) itemView.findViewById(R.id.textViewDetails);
        }
    }
}
