package net.dentare.akibamapandroid.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import net.dentare.akibamapandroid.R;
import net.dentare.akibamapandroid.fragment.MyPostFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseSubActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showProgressDialog();

        setAuthListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                CircleImageView iconImageView = (CircleImageView) findViewById(R.id.profile_icon);
                TextView nameTextView = (TextView) findViewById(R.id.profile_name);
                TextView idTextView = (TextView) findViewById(R.id.profile_id);
                if (user == null){
                    finish();
                    return;
                } else {
                    Uri uri = user.getPhotoUrl();
                    String name = user.getDisplayName();
                    if (uri != null) Picasso.with(ProfileActivity.this).load(uri).into(iconImageView);
                    if (name != null) nameTextView.setText(name);
                    idTextView.setText(getString(R.string.profile_default_id_template,user.getUid()));
                }
                hideProgressDialog();
            }
        });

        FirebaseUser user = getAuth().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_add, MyPostFragment.getInstance(user.getUid()), "fragment");
        transaction.commit();
    }
}