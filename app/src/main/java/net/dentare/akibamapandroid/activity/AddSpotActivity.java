package net.dentare.akibamapandroid.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.fragment.ImageSwipeFragment;
import net.dentare.akibamapandroid.resources.Category;
import net.dentare.akibamapandroid.resources.Spot;
import net.dentare.akibamapandroid.resources.SpotImage;
import net.dentare.akibamapandroid.util.Config;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AddSpotActivity extends BaseSubActivity implements OnMapReadyCallback,ImageSwipeFragment.ImageSwipeListener {
    private long id;
    private Spot spot;
    private GoogleMap map;
    private Marker marker;
    private static final int REQUEST_PLACE_PICKER = 999;
    private static final int REQUEST_PICTURE_CONTENT = 888;
    private static final int REQUEST_KK_PICTURE_CONTENT = 777;
    private final List<Category> categoryList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUser user = getAuth().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);

        Button buttonSelectCategory = (Button) findViewById(R.id.buttonSelectCategory);
        buttonSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategorySelectDialogFragment dialogFragment = CategorySelectDialogFragment.getInstance(categoryList,spot);
                dialogFragment.show(getSupportFragmentManager(),"Category");
            }
        });

        Button buttonSelectImage = (Button) findViewById(R.id.buttonSelectImage);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19){
                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_KK_PICTURE_CONTENT);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference database = getDatabase();
                showProgressDialog();
                if (id <= -1) {
                    final Query query = database.child(Config.firebaseSpot).orderByKey().limitToLast(1);
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Spot spot = dataSnapshot.getValue(Spot.class);
                            if (spot != null && id <= -1) {
                                id = spot.getId() + 1;
                            }
                            updateValue();
                            query.removeEventListener(this);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    updateValue();
                }
            }
        });
        showProgressDialog();
        DatabaseReference database = getDatabase();
        database.child(Config.firebaseCategory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Category> categories = dataSnapshot.getValue(new GenericTypeIndicator<List<Category>>() {
                });
                categoryList.addAll(categories);
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
        if (id < 0) {
            spot = new Spot();
            spot.setCategoryId(new LinkedList<Long>());
            spot.setImages(new LinkedList<SpotImage>());
            initImages();
            setCategoryTextView();
            return;
        }
        database.child(Config.firebaseSpot).child(String.valueOf(id)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Spot> type = new GenericTypeIndicator<Spot>() {};
                Spot tmp = dataSnapshot.getValue(type);
                if (tmp == null)
                    return;
                spot = tmp;
                final FirebaseUser user = getAuth().getCurrentUser();
                if (user == null) {
                    finish();
                    return;
                }
                getDatabase().child(Config.firebaseAdmin).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (user.getUid() != spot.getUserId() && !dataSnapshot.getValue(boolean.class)){
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                setCategoryTextView();
                initImages();
                EditText editTextName = (EditText) findViewById(R.id.editTextName);
                if (editTextName != null) editTextName.setText(spot.getName());
                EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);
                if (editTextAddress != null) editTextAddress.setText(spot.getAddress());
                EditText editTextDetails = (EditText) findViewById(R.id.editTextDetails);
                if (editTextDetails != null) editTextDetails.setText(spot.getDetail());
                EditText editTextUrl = (EditText) findViewById(R.id.editTextUrl);
                if (editTextUrl != null) editTextUrl.setText(spot.getUrl());
                setMapLatLng();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                finish();
            }
        });
    }

    private void setMapLatLng(LatLng latLng) {
        if (map == null && latLng == null) return;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        if (marker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(String.format(Locale.ENGLISH, "%.10f:%.10f", latLng.longitude, latLng.latitude));
            marker = map.addMarker(markerOptions);
            return;
        }
        marker.setPosition(latLng);
        marker.setTitle(String.format(Locale.ENGLISH, "%.10f:%.10f", latLng.longitude, latLng.latitude));
    }

    private void updateValue(){
        View view = findViewById(R.id.nestedScrollView);
        if (view == null) return;
        List<SpotImage> spotImages = spot.getImages();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
        if (spot.getCategoryId().size() <= 0) {
            Snackbar.make(view, R.string.add_spot_category_not_found_message, Snackbar.LENGTH_INDEFINITE).setAction(R.string.okay, listener).show();
            hideProgressDialog();
            return;
        }
        if (marker == null) {
            Snackbar.make(view, R.string.add_spot_pin_not_found, Snackbar.LENGTH_INDEFINITE).show();
            hideProgressDialog();
            return;
        }
        if (spotImages.size() <= 0) {
            Snackbar.make(view, R.string.add_spot_image_not_found_or_photo_x1_less, Snackbar.LENGTH_INDEFINITE).setAction(R.string.okay, listener).show();
            hideProgressDialog();
            return;
        }
        FirebaseUser user = getAuth().getCurrentUser();
        if (user == null) return;
        String name = "",address = "",details = "",url = "";
        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        if (editTextName != null) name = editTextName.getText().toString();
        EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        if (editTextAddress != null) address = editTextAddress.getText().toString();
        EditText editTextDetails = (EditText) findViewById(R.id.editTextDetails);
        if (editTextDetails != null) details = editTextDetails.getText().toString();
        EditText editTextUrl = (EditText) findViewById(R.id.editTextUrl);
        if (editTextUrl != null) url = editTextUrl.getText().toString();

        if (name.length() <= 0|| address.length() <= 0 || details.length() <= 0) {
            Snackbar.make(view, R.string.add_spot_required_blank, Snackbar.LENGTH_INDEFINITE).setAction(R.string.okay, listener).show();
            hideProgressDialog();
            return;
        }else if (!url.matches("^https?://.+?\\..+")){
            Snackbar.make(view, R.string.add_spot_url_not_match, Snackbar.LENGTH_INDEFINITE).setAction(R.string.okay, listener).show();
            hideProgressDialog();
            return;
        }
        spot.setId(id);
        spot.setName(name);
        spot.setAddress(address);
        spot.setDetail(details);
        spot.setUrl(url);

        LatLng latLng = marker.getPosition();
        spot.setLat(latLng.latitude);
        spot.setLng(latLng.longitude);
        if (spot.getUserId() == null) spot.setUserId(user.getUid());
        boolean flag = false;
        for (int i = 0; i < spotImages.size(); i++) {
            final int value = i;
            final SpotImage spotImage = spotImages.get(i);
            if (spotImage.getUrl().startsWith(Config.firebaseImageSpacer)) continue;
            else flag = true;
            Uri uri = Uri.parse(spotImage.getUrl());
            StorageReference access = getStorage().child(Config.firebaseSpot).child(String.valueOf(id)).child(String.valueOf((long)(Math.random() * Long.MAX_VALUE))+"."+getMIMEType(uri.toString()));
            UploadTask uploadTask = access.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = taskSnapshot.getDownloadUrl();
                    if (uri == null) return;
                    String string = Config.firebaseImageSpacer+uri.toString();
                    spotImage.setUrl(string);
                    for (SpotImage spotImage: spot.getImages()) {
                        if (!spotImage.getUrl().startsWith(Config.firebaseImageSpacer)){
                            return;
                        }
                    }
                    updateValueStep2();
                }
            });
        }
        if (!flag) updateValueStep2();
    }

    private void updateValueStep2(){
        getDatabase().child(Config.firebaseSpot).child(String.valueOf(id)).setValue(spot);
        hideProgressDialog();
        finish();
    }

    private void setCategoryTextView(){
        TextView textView = (TextView) findViewById(R.id.textViewCategory);
        if (textView != null && spot != null) textView.setText(getCategoryListToString());
    }

    private String getCategoryListToString(){
        String string = "";
        for (Category category : categoryList) {
            if (spot.getCategoryId().contains(category.getId()))
                string += ","+category.getName();
        }
        if (string == "")
            string = ","+getString(R.string.add_spot_category_not_found);
        return string.substring(1);
    }

    private void setMapLatLng() {
        if (spot == null) return;
        setMapLatLng(new LatLng(spot.getLat(), spot.getLng()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_spot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add_spot_search) {
            try {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                Intent intent = intentBuilder.build(this);
                startActivityForResult(intent, REQUEST_PLACE_PICKER);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PLACE_PICKER && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            String name = place.getName().toString();
            String address = place.getAddress().toString();
            String url = place.getWebsiteUri().toString();
            LatLng latLng = place.getLatLng();

            EditText editTextName = (EditText) findViewById(R.id.editTextName);
            if (editTextName != null) editTextName.setText(name);
            EditText editTextAddress = (EditText) findViewById(R.id.editTextAddress);
            if (editTextAddress != null) editTextAddress.setText(address);
            EditText editTextUrl = (EditText) findViewById(R.id.editTextUrl);
            if (editTextUrl != null) editTextUrl.setText(url);
            setMapLatLng(latLng);
            return;
        }
        String path = null;
        if (requestCode == REQUEST_KK_PICTURE_CONTENT && Build.VERSION.SDK_INT >= 19 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(uri, takeFlags);
            path = uri.toString();
        } else if(requestCode == REQUEST_PICTURE_CONTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            path = uri.toString();
        }

        if ((requestCode == REQUEST_KK_PICTURE_CONTENT || requestCode == REQUEST_PICTURE_CONTENT) && resultCode == RESULT_OK && path != null && spot != null) {
            String mime = getMIMEType(data.getData().toString());
            if (mime != null && mime.startsWith("image/")){
                spot.getImages().add(new SpotImage(Config.localImageSpacer+path));
                initImages();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initImages(){
        ImageSwipeFragment fragment = (ImageSwipeFragment) getSupportFragmentManager().findFragmentById(R.id.imageSwipeFragment);
        if (fragment != null && spot != null) {
            fragment.clear();
            fragment.addAll(spot.getImages());
            fragment.notifyDataSetChanged();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setMapLatLng(latLng);
            }
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Config.akiba_lat,Config.akiba_lng),Config.akiba_z));
    }

    @Override
    public void onClickItem(View view, int position) {
        ImageSwipeFragment fragment = (ImageSwipeFragment) getSupportFragmentManager().findFragmentById(R.id.imageSwipeFragment);
        if (fragment != null && fragment.length() > position) {
            fragment.remove(position);
            fragment.notifyDataSetChanged();
        }
    }

    public static class CategorySelectDialogFragment extends DialogFragment{
        private List<Category> categoryList = new LinkedList<>();
        private Spot spot = new Spot();

        public static CategorySelectDialogFragment getInstance(List<Category> categoryList,Spot spot){
            CategorySelectDialogFragment categorySelectDialogFragment = new CategorySelectDialogFragment();
            categorySelectDialogFragment.categoryList = categoryList;
            categorySelectDialogFragment.spot = spot;
            return categorySelectDialogFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            int size = categoryList.size() - 1;
            String[] strings = new String[size];
            boolean[] checked = new boolean[size];
            for (int i = 0; i < strings.length; i++) {
                Category category = categoryList.get(i+1);
                strings[i] = category.getName();
                checked[i] = spot.getCategoryId().contains(category.getId());
            }
            builder.setMultiChoiceItems(strings, checked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    Category category = categoryList.get(which+1);
                    List<Long> categoryIds = spot.getCategoryId();
                    if (isChecked)
                        categoryIds.add(category.getId());
                    else
                        categoryIds.remove(category.getId());
                    FragmentActivity activity = getActivity();
                    if (activity instanceof AddSpotActivity){
                        ((AddSpotActivity)activity).setCategoryTextView();
                    }
                }
            });
            return builder.create();
        }
    }

    private String getMIMEType(String path){
        File file = new File(path);
        String fn = file.getName();
        int ch = fn.lastIndexOf('.');
        String ext = (ch>=0)?fn.substring(ch + 1):null;
        if (ext == null) return null;
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
    }
}
