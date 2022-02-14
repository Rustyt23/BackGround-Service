package com.rustyt.bgservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    Intent mServiceIntent;
    private YourService mYourService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Sync sync = new Sync(call,60*1000);


        mYourService = new YourService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            Log.i("MainActivity Listened", "Service tried to stop");

            startService(mServiceIntent);
        }
    }

    final private Runnable call = new Runnable() {
        public void run() {
            //This is where my sync code will be, but for testing purposes I only have a Log statement
            Log.v("test","this will run every minute");
            handler.postDelayed(call,60*1000);
        }
    };
    public final Handler handler = new Handler();
public class Sync {


    Runnable task;

    public Sync(Runnable task, long time) {
        this.task = task;
        handler.removeCallbacks(task);
        handler.postDelayed(task, time);
    }
}
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }


    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Log.i("Broadcast Listened", "Service tried to destroyed");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }
}