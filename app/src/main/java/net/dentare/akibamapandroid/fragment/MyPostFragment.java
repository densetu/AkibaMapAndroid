package net.dentare.akibamapandroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.adapter.MyPostRecyclerViewAdapter;
import net.dentare.akibamapandroid.resources.Spot;
import net.dentare.akibamapandroid.util.Config;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MyPostFragment extends Fragment{
    private final List<Spot> spots = new LinkedList<>();
    private MyPostRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private String userId;
    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            spots.clear();
            Iterator<DataSnapshot> tmp = dataSnapshot.getChildren().iterator();
            if (tmp == null) return;
            while (tmp.hasNext()){
                Spot spot = tmp.next().getValue(Spot.class);
                spots.add(spot);
            }
            notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public static MyPostFragment getInstance(String userId) {
        MyPostFragment fragment = new MyPostFragment();
        fragment.userId = userId;
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapter == null){
            adapter = new MyPostRecyclerViewAdapter(getActivity(),spots);
            if (recyclerView != null) recyclerView.setAdapter(adapter);
        }
        reference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (reference != null) reference.child(Config.firebaseSpot).orderByChild(Config.firebaseUserId).equalTo(userId).addValueEventListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (reference != null) reference.child(Config.firebaseSpot).orderByChild(Config.firebaseUserId).equalTo(userId).removeEventListener(listener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_post,container,false);
        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            if (adapter != null) recyclerView.setAdapter(adapter);
        }
        return view;
    }

    public void notifyDataSetChanged() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}