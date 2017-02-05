package net.dentare.akibamapandroid.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.adapter.RankingPagerAdapter;
import net.dentare.akibamapandroid.resources.Spot;
import net.dentare.akibamapandroid.resources.SpotRanking;
import net.dentare.akibamapandroid.util.Config;

import java.util.LinkedList;
import java.util.List;

public class RankingActivity extends BaseSubActivity {
    private RankingPagerAdapter adapter;
    private ViewPager viewPager;
    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (adapter != null){
                adapter.addAll(dataSnapshot.getValue(new GenericTypeIndicator<List<SpotRanking>>() {}));
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        if (viewPager == null) viewPager = (ViewPager) findViewById(R.id.viewPager);
        if (viewPager != null){
            adapter = RankingPagerAdapter.getInstance(getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getDatabase().child(Config.firebaseRanking).addValueEventListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        getDatabase().child(Config.firebaseRanking).removeEventListener(listener);
    }
}
