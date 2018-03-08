package edu.asu.cidse.mc.group2;

/**
 * Created by jlee375 on 2016-02-03.
 */
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class SensorHandlerClass extends Service implements SensorEventListener {

    private SensorManager accelManage;
    private Sensor senseAccel;
    float accelValuesX[] = new float[128];
    float accelValuesY[] = new float[128];
    float accelValuesZ[] = new float[128];
    int index = 0;
    int k=0;
    Bundle b;

    private static String TAG = "SensorHandlerClass";

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO Auto-generated method stub
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            index++;
            Log.d(TAG, "Sensor changed() method called...");
            float accX = sensorEvent.values[0];
            float accY = sensorEvent.values[1];
            float accZ = sensorEvent.values[2];

            long time = new Date().getTime();


            Log.d(TAG, "Time"+ time+ "\t acc X"+ accX+"\tacc Y"+ accY +"\t acc Z"+ accZ);

            //Insert the record to the database

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate(){
        //Toast.makeText(this, "Accelerometer data collection Service Started", Toast.LENGTH_LONG).show();
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, senseAccel, 1000000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //b = intent.getExtras();
        //String phoneNumber = b.getString("phone");
        //Toast.makeText(SensorHandlerClass.this, phoneNumber, Toast.LENGTH_LONG).show();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        //k = 0;
        Log.d(TAG, "Service started....");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
