package net.dentare.akibamapandroid.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import net.dentare.akibamapandroid.R;

public class AddSpotActivity extends BaseSubActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
