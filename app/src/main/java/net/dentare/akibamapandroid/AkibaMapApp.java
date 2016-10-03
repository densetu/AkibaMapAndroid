package net.dentare.akibamapandroid;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class AkibaMapApp extends Application{
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
