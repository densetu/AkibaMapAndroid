package net.dentare.akibamapandroid.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BaseSubActivity extends BaseActivity {
    @Override
    protected void onStart() {
        super.onStart();
        ActionBar bar = getSupportActionBar();
        if(bar == null)
            return;
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean bool = true;
        if (item.getItemId() == android.R.id.home)
            finish();
        else
            bool = super.onOptionsItemSelected(item);
        return bool;
    }
}
