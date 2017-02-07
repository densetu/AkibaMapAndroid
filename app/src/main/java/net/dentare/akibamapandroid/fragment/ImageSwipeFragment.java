package net.dentare.akibamapandroid.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.adapter.ImageSwipeAdapter;
import net.dentare.akibamapandroid.resources.SpotImage;

import java.util.LinkedList;
import java.util.List;

public class ImageSwipeFragment extends Fragment {
    private ImageSwipeListener listener;
    private List<SpotImage> spotImages = new LinkedList<>();
    private ImageSwipeAdapter adapter;
    private RecyclerView recyclerView;
    private ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }
    };

    public interface ImageSwipeListener{
        void onClickItem(View view,int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImageSwipeListener){
            listener = (ImageSwipeListener) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        spotImages = new LinkedList<>();
        adapter = new ImageSwipeAdapter(getActivity(), spotImages, listener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_swipe, container, false);
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);

            if (adapter != null && adapter.getItemCount() > 1)
                new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
        }
        return view;
    }

    public void addAll(List<SpotImage> spotImages){
        this.spotImages.addAll(spotImages);
    }

    public void remove(int index){
        this.spotImages.remove(index);
    }

    public int length(){
        return spotImages.size();
    }

    public void clear(){
        this.spotImages.clear();
    }

    public void notifyDataSetChanged() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}
