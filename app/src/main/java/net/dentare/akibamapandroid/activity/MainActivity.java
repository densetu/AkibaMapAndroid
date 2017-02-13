package net.dentare.akibamapandroid.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.resources.Category;
import net.dentare.akibamapandroid.resources.Spot;
import net.dentare.akibamapandroid.util.Config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback{
    private final List<Category> categoryList = new LinkedList<>();
    private final List<Spot> spotList = new LinkedList<>();
    private final HashMap<Long,Marker> markerList = new HashMap<>();
    private FirebaseUser mUser;
    private GoogleMap map;
    private View navigationHeaderView;
    private long nowSelectCategory = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setAuthListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mUser = user;
                if (navigationHeaderView != null) {
                    CircleImageView iconImageView = (CircleImageView) navigationHeaderView.findViewById(R.id.profile_icon);
                    TextView nameTextView = (TextView) navigationHeaderView.findViewById(R.id.profile_name);
                    TextView idTextView = (TextView) navigationHeaderView.findViewById(R.id.profile_id);
                    if (user == null){
                        iconImageView.setVisibility(View.GONE);
                        nameTextView.setText(R.string.profile_default_name);
                        idTextView.setText(R.string.profile_default_id);
                    } else {
                        iconImageView.setVisibility(View.VISIBLE);
                        Uri uri = user.getPhotoUrl();
                        String name = user.getDisplayName();
                        if (uri != null) Picasso.with(MainActivity.this).load(uri).into(iconImageView);
                        if (name != null) nameTextView.setText(name);
                        idTextView.setText(getString(R.string.profile_default_id_template,user.getUid()));
                    }
                }
                setCategoryList();
                hideProgressDialog();
                isVisibleMenuLoginLogout(user != null);
            }
        });

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final DatabaseReference database = getDatabase();

        database.child(Config.firebaseSpot).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Spot>> type = new GenericTypeIndicator<List<Spot>>(){};
                List<Spot> tmp = dataSnapshot.getValue(type);
                if (tmp == null)
                    return;
                synchronized (spotList) {
                    spotList.clear();
                    spotList.addAll(tmp);

                    if (map == null)
                        return;
                    for (int i = 0; i < spotList.size(); i++) {
                        Spot spot = spotList.get(i);
                        Marker marker = markerList.get(spot.getId());
                        if ( marker != null) {
                            marker.setTitle(spot.getName());
                            marker.setVisible(getCheckCategory(spot));
                            marker.setPosition(new LatLng(spot.getLat(),spot.getLng()));
                            marker.setVisible(getCheckCategory(spot));
                            continue;
                        }
                        MarkerOptions options = new MarkerOptions();
                        options.title(spot.getName());
                        options.position(new LatLng(spot.getLat(),spot.getLng()));
                        options.visible(getCheckCategory(spot));
                        marker = map.addMarker(options);
                        markerList.put(spot.getId(),marker);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationHeaderView = navigationView.getHeaderView(navigationView.getHeaderCount()-1);
        if (navigationHeaderView != null) navigationHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser != null) startIntent(ProfileActivity.class);
            }
        });

        setCategoryList();
    }

    private void setCategoryList(){
        final FirebaseUser user = getAuth().getCurrentUser();
        final DatabaseReference database = getDatabase();
        if (user != null) database.child(Config.firebaseAdmin).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setCategoryListStep2(dataSnapshot.exists() ? dataSnapshot.getValue(boolean.class) : false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        else setCategoryListStep2(false);
    }

    private void isVisibleMenuLoginLogout(boolean isLogin){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView == null) return;
        Menu menu = navigationView.getMenu();
        if (menu == null) return;
        MenuItem menuLogin = menu.findItem(R.id.nav_login);
        if (menuLogin != null) menuLogin.setVisible(!isLogin);
        MenuItem menuLogout = menu.findItem(R.id.nav_logout);
        if (menuLogout != null) menuLogout.setVisible(isLogin);
    }

    private void setCategoryListStep2(final boolean value){
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        getDatabase().child(Config.firebaseCategory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Category>> type = new GenericTypeIndicator<List<Category>>(){};
                List<Category> tmp = dataSnapshot.getValue(type);
                if (tmp == null)
                    return;
                synchronized (categoryList) {
                    categoryList.clear();
                    categoryList.addAll(tmp);

                    Menu menu = navigationView.getMenu();
                    menu.clear();
                    menu.setGroupCheckable(R.id.nav_category, true, true);
                    for (int i = 0; i < categoryList.size(); i++) {
                        Category category = categoryList.get(i);
                        MenuItem menuItem = menu.add(R.id.nav_category, (int) category.getId(), Menu.NONE, category.getName());
                        menuItem.setCheckable(true);
                        menuItem.setChecked(i == nowSelectCategory);
                        menuItem.setIcon(R.drawable.ic_place_black);
                    }
                    getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
                    menu.findItem(R.id.nav_add_spot).setVisible(value);
                    isVisibleMenuLoginLogout(mUser != null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void startIntent(Class myClass){
        Intent intent = new Intent(MainActivity.this,myClass);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        for (Category category : categoryList) {
            if (category.getId() == id) {
                nowSelectCategory = id;
                checkCategoryAndSetMarker();
                break;
            }
        }
        if (id == R.id.nav_add_spot) {
            startIntent(AddSpotActivity.class);
        } else if (id == R.id.nav_ranking) {
            startIntent(RankingActivity.class);
        } else if (id == R.id.nav_login && mUser == null) {
            startIntent(LoginActivity.class);
        } else if (id == R.id.nav_logout && mUser != null) {
            getAuth().signOut();
            LoginManager.getInstance().logOut();
            Twitter.logOut();
        } else if (id == R.id.nav_exit) {
            finish();
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (Map.Entry<Long,Marker> entry: markerList.entrySet()) {
                    if (entry.getValue().equals(marker)){
                        Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
                        intent.putExtra("id",entry.getKey());
                        startActivity(intent);
                    }
                }
            }
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Config.akiba_lat,Config.akiba_lng),Config.akiba_z));
    }

    private boolean getCheckCategory(Spot spot){
        boolean flag = false;
        if (nowSelectCategory == 0) return true;
        for (long id: spot.getCategoryId()){
            if (nowSelectCategory == id) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void checkCategoryAndSetMarker(){
        for (Spot spot:spotList) {
            Marker marker = markerList.get(spot.getId());
            if (marker != null)
                marker.setVisible(getCheckCategory(spot));
        }
    }
}
