package edu.asu.cidse.mc.group2;

/**
 * Created by jlee375 on 2016-02-03.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static edu.asu.cidse.mc.group2.MainActivityFragment.LABEL;
import static edu.asu.cidse.mc.group2.MainActivityFragment.TABLE_NAME;

public class SensorHandlerClass extends Service implements SensorEventListener {

    private SensorManager accelManage;
    private Sensor senseAccel;
    int index = 0;
    int k = 0;
    Bundle b;
    GraphDatabase graphDatabase;
    String tableName;
    static int ACCE_FILTER_DATA_MIN_TIME = 0;
    long lastSaved = System.currentTimeMillis();
    List<AccSample> accSamples;
    private static String TAG = "SensorHandlerClass";
    public static final String INTENT_FILTER = "AccBroadCast";
    public static final String ACC_X = "x";
    public static final String ACC_Y = "y";
    public static final String ACC_Z = "z";
    public static final String HIDE = "hide";
    private int label =-1;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service stopping..onDestroy() called...");
        accelManage.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO Auto-generated method stub
        Sensor mySensor = sensorEvent.sensor;
        {
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            Log.d(TAG, "Sensor changed() method called...");
            float accX = sensorEvent.values[0];
            float accY = sensorEvent.values[1];
            float accZ = sensorEvent.values[2];
            Bundle bundle = new Bundle();
            long time = new Date().getTime();

            Log.d(TAG, "Time" + time + "\t acc X" + accX + "\tacc Y" + accY + "\t acc Z" + accZ);

            //Insert the record to the database
            if (graphDatabase != null) {
                accSamples.add(new AccSample(accX,accY,accZ));
                if(accSamples.size() == 50) {
                    graphDatabase.insertrecords(tableName, accSamples,label);
                     Log.d(TAG, "Insertion done...");
                     bundle.putString(HIDE,"true");
                    Intent updateUI = new Intent(INTENT_FILTER);

                    //updateUI.addCategory(Intent.CATEGORY_DEFAULT);
                    bundle.putFloat(ACC_X, accX);
                    bundle.putFloat(ACC_Y, accY);
                    bundle.putFloat(ACC_Z, accZ);

                    updateUI.putExtras(bundle);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(updateUI);
                     stopSelf();
                }

                //Send broadcast to the fragment for updating UI
                Intent updateUI = new Intent(INTENT_FILTER);

                //updateUI.addCategory(Intent.CATEGORY_DEFAULT);

                bundle.putFloat(ACC_X, accX);
                bundle.putFloat(ACC_Y, accY);
                bundle.putFloat(ACC_Z, accZ);
                bundle.putString(HIDE,"false");

                updateUI.putExtras(bundle);
                //sendBroadcast(updateUI);
                //sendBroadcast(updateUI);
                LocalBroadcastManager.getInstance(this).sendBroadcast(updateUI);
            }
        }}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate() {
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_FASTEST);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        b = intent.getExtras();
        //String phoneNumber = b.getString("phone");
        //Toast.makeText(SensorHandlerClass.this, phoneNumber, Toast.LENGTH_LONG).show();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        //k = 0;
        accSamples = new ArrayList<AccSample>();

        if (b != null) {
            b = intent.getExtras();
            tableName = b.getString(TABLE_NAME);
            label = b.getInt(LABEL);

            graphDatabase = new GraphDatabase(this, tableName);
            graphDatabase.open();
            graphDatabase.createTableIfNotExists();
            Log.d(TAG, "Service started....");
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
