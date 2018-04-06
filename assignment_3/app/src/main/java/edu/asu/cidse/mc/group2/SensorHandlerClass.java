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

import java.util.Date;

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

    private static String TAG = "SensorHandlerClass";
    public static final String INTENT_FILTER = "AccBroadCast";
    public static final String ACC_X = "x";
    public static final String ACC_Y = "y";
    public static final String ACC_Z = "z";

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
        if ((System.currentTimeMillis() - lastSaved) > ACCE_FILTER_DATA_MIN_TIME)
        {
            lastSaved = System.currentTimeMillis();
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            Log.d(TAG, "Sensor changed() method called...");
            float accX = sensorEvent.values[0];
            float accY = sensorEvent.values[1];
            float accZ = sensorEvent.values[2];

            long time = new Date().getTime();

            Log.d(TAG, "Time" + time + "\t acc X" + accX + "\tacc Y" + accY + "\t acc Z" + accZ);

            //Insert the record to the database
            if (graphDatabase != null) {
                graphDatabase.insertrecords(tableName, time, accX, accY, accZ);
                Log.d(TAG, "Insertion done...");

                //Send broadcast to the fragment for updating UI
                Intent updateUI = new Intent(INTENT_FILTER);

                //updateUI.addCategory(Intent.CATEGORY_DEFAULT);

                Bundle bundle = new Bundle();
                bundle.putFloat(ACC_X, accX);
                bundle.putFloat(ACC_Y, accY);
                bundle.putFloat(ACC_Z, accZ);

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
        accelManage.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        b = intent.getExtras();
        //String phoneNumber = b.getString("phone");
        //Toast.makeText(SensorHandlerClass.this, phoneNumber, Toast.LENGTH_LONG).show();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        //k = 0;

        if (b != null) {
            b = intent.getExtras();
            tableName = b.getString(TABLE_NAME);

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