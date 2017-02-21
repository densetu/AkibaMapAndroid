package net.dentare.akibamapandroid.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.fragment.ImageSwipeFragment;
import net.dentare.akibamapandroid.resources.Spot;
import net.dentare.akibamapandroid.resources.SpotAccess;
import net.dentare.akibamapandroid.util.Config;

import java.util.List;

public class DetailsActivity extends BaseSubActivity implements ImageSwipeFragment.ImageSwipeListener, OnMapReadyCallback {
    private long id;
    private Spot spot;
    private GoogleMap map;
    private Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DatabaseReference database = getDatabase();

        Intent intent = getIntent();
        id = intent.getLongExtra("id",-1);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);

        FirebaseUser user = getAuth().getCurrentUser();
        if (user == null) button.setVisibility(View.GONE);
        else database.child(Config.firebaseAdmin).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value = dataSnapshot.exists() ? dataSnapshot.getValue(boolean.class) : false;
                if (!value)
                    findViewById(R.id.fab).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (id < 0) {
            finish();
            return;
        }

        showProgressDialog();
        final DatabaseReference accessRef = database.child(Config.firebaseSpot).child(String.valueOf(id)).child(Config.firebaseAccess);
        accessRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accessRef.removeEventListener(this);
                SpotAccess access = dataSnapshot.getValue(SpotAccess.class);
                access.setCount(access.getCount()+1);
                accessRef.setValue(access);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                accessRef.removeEventListener(this);
            }
        });
        database.child(Config.firebaseSpot).child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Spot> type = new GenericTypeIndicator<Spot>(){};
                Spot tmp = dataSnapshot.getValue(type);
                if (tmp == null)
                    return;
                spot = tmp;
                TextView textViewDetails = (TextView) findViewById(R.id.textViewDetails);
                if (textViewDetails != null) textViewDetails.setText(spot.getDetail());
                TextView textViewUrl = (TextView) findViewById(R.id.textViewUrl);
                if (textViewUrl != null){
                    textViewUrl.setText(spot.getUrl());
                    textViewUrl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (spot != null) startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(spot.getUrl())));
                        }
                    });
                }
                TextView textViewAddress = (TextView) findViewById(R.id.textViewAddress);
                if (textViewAddress != null) textViewAddress.setText(spot.getAddress());
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) actionBar.setTitle(spot.getName());

                ImageSwipeFragment fragment = (ImageSwipeFragment) getSupportFragmentManager().findFragmentById(R.id.imageSwipeFragment);
                if (fragment != null) {
                    fragment.clear();
                    fragment.addAll(spot.getImages());
                    fragment.notifyDataSetChanged();
                }
                setMarker();

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this,AddSpotActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onClickItem(View view, int position) {
        Log.d("log",String.valueOf(position));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        setMarker();
    }

    private void setMarker(){
        if (map != null && spot != null) {
            LatLng latLng = new LatLng(spot.getLat(),spot.getLng());
            if (marker == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(spot.getName());
                markerOptions.position(latLng);
                marker = map.addMarker(markerOptions);
            }else {
                marker.setTitle(spot.getName());
                marker.setPosition(latLng);
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17.0f));
        }
    }
}
