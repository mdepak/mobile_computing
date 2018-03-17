package edu.asu.cidse.mc.group2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.http.AndroidHttpClient;
import android.net.http.HttpsConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.android.internal.http.multipart.MultipartEntity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static edu.asu.cidse.mc.group2.GraphDatabase.DBNAME;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    private static final String TAG = "MainActivityFragment";
    // View f   or the graph showing data

    private  com.jjoe64.graphview.GraphView graphView;
    UpdateThread updateThread;
    Queue<Float> valList;
    LineGraphSeries<DataPoint> xSeries;
    LineGraphSeries<DataPoint> ySeries;
    LineGraphSeries<DataPoint> zSeries;

    public int value = 0;

    public static final String TABLE_NAME = "table_name";

    // Horizontal axis values
    String[] horAxis = {"2700", "2750", "2800", "2850", "2900", "2950", "3000", "3150", "3200"};

    // Vertical axis values
    String[] verAxis = {"500", "1000", "1500", "2000"};

    public MainActivityFragment() {
    }

    private String getTableName(String patientId, String age, String name, String sex) {
        return name + "_" + patientId + "_" + age + "_" + sex;
    }

    private void updateGraph(float X, float Y, float Z)
    {
        xSeries.appendData(new DataPoint(value, X), true , 10);
        ySeries.appendData(new DataPoint(value, Y), true , 10);
        zSeries.appendData(new DataPoint(value, Z), true , 10);
        value++;
    }

    BroadcastReceiver receiver;



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Recieved broadcast message ");

                Bundle bundle = intent.getExtras();
                float accX = bundle.getFloat(SensorHandlerClass.ACC_X);
                float accY = bundle.getFloat(SensorHandlerClass.ACC_Y);
                float accZ = bundle.getFloat(SensorHandlerClass.ACC_Z);

                updateGraph(accX, accY, accZ);

                Log.d(TAG, "Recieved data in message "+ accX);
            }
        };


      //getContext().registerReceiver(receiver, new IntentFilter(SensorHandlerClass.INTENT_FILTER));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(SensorHandlerClass.INTENT_FILTER));
        float[] values = new float[10];
        valList = new LinkedList();

        // Initialize the values to be zero
        for (int i = 0; i < 10; i++) {
            float val = new Random().nextFloat();
            val = 0;
            values[i] = val;
            valList.add(val);
        }

        RelativeLayout rootLayout = rootView.findViewById(R.id.rel_layout);

        // Initialize view in the onCreate and add to the layout
        graphView =  rootView.findViewById(R.id.graph);

        xSeries = new LineGraphSeries<>(new DataPoint[] {});
        xSeries.setTitle("Acc X");
        xSeries.setColor(Color.GREEN);



        ySeries = new LineGraphSeries<>(new DataPoint[] {});
        ySeries.setTitle("Acc Y");
        ySeries.setColor(Color.RED);

        zSeries = new LineGraphSeries<>(new DataPoint[] {});
        zSeries.setTitle("Acc Z");
        zSeries.setColor(Color.BLUE);

        graphView.addSeries(xSeries);
        graphView.addSeries(ySeries);
        graphView.addSeries(zSeries);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(10);
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        Button runButton = rootView.findViewById(R.id.runBtn);
        Button stopButton = rootView.findViewById(R.id.stopBtn);
        Button downloadButton = rootView.findViewById(R.id.downloadBtn);
        Button uploadButton = rootView.findViewById(R.id.uploadBtn);

        // OnClickListener for the download button
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String url =  "http://impact.asu.edu/CSE535Spring18Folder/";

            }
        });
        // OnClickListener for the upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Upload_File().execute();
                System.out.print("Uploaded..!");
            }
        });

        // OnClickListener for the run button click
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             *  Handles the run button click
             *  Starts the thread if its not alive and the thread updates the view with random generated data
             *
             */
            public void onClick(View view) {
                EditText id = ((EditText) rootView.findViewById(R.id.editText));
                String patientId = id.getText().toString();

                EditText Age = ((EditText) rootView.findViewById(R.id.editText4));
                String age = Age.getText().toString();

                EditText Name = ((EditText) rootView.findViewById(R.id.editText2));
                String name = Name.getText().toString();

                RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.radioGroup);
                int selectedId = rg.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) rootView.findViewById(selectedId);
                String sex = radioButton.getText().toString();
                boolean isValidInput = true;

                if (patientId.length() == 0) {
                    id.setError("Id is required!");
                    isValidInput = false;
                }

                if (age.length() == 0) {
                    Age.setError("Age is required!");
                    isValidInput = false;
                }

                if (name.length() == 0) {
                    Name.setError("Name is required!");
                    isValidInput = false;
                }

                String tableName = getTableName(patientId, age, name, sex);


                if (isValidInput && (updateThread == null || !updateThread.isAlive())) {
                    updateThread = new UpdateThread(graphView, valList);
                    //updateThread.start();
                    graphView.setVisibility(View.VISIBLE);

                    Intent sensorService = new Intent(getContext(), SensorHandlerClass.class);
                    Bundle b = new Bundle();
                    b.putString(TABLE_NAME, tableName);
                    sensorService.putExtras(b);
                    getActivity().startService(sensorService);
                }
            }
        });

        // OnClickListener for the Stop button click
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             *  Handles the stop button click
             *
             *  Stops the thread by signalling and clears the view
             */
            public void onClick(View view) {
                if (updateThread != null) {
                    Intent sensorService = new Intent(getContext(), SensorHandlerClass.class);
                    getActivity().stopService(sensorService);

                    valList = updateThread.getValues();

                    updateThread.signalStop();
                    updateThread = null;
                    graphView.setVisibility(View.INVISIBLE);
                }
            }
        });
        return rootView;
    }


    private class Upload_File extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... unsued) {
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://impact.asu.edu/CSE535Spring18Folder");

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                File f = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "Android/Data/CSE535_ASSIGNMENT2" +
                        File.separator + DBNAME);

                if(f != null)
                {
                    entityBuilder.addPart("uploaded_file", new FileBody(f));
                }

                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);
                Log.v("result", result);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            /*File f = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "Android/Data/CSE535_ASSIGNMENT2" +
                    File.separator + DBNAME);
            try {
                byte[] bytearray = org.apache.commons.io.FileUtils.readFileToByteArray(f);
                HttpPost httpPost = new HttpPost("http://impact.asu.edu/CSE535Spring18Folder");
                HttpClient httpclient = new DefaultHttpClient();
                httpPost.setEntity(new ByteArrayEntity(bytearray));
                HttpResponse response = httpclient.execute(httpPost);
                StringBuilder sbr = new StringBuilder(response.getStatusLine().getStatusCode());
                Log.d(TAG,sbr.toString());
                Log.d(TAG,"Uploaded");
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            /*String url = "http://impact.asu.edu/CSE535Spring18Folder";
            //Environment.getExternalStorageDirectory().getAbsolutePath(),

            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(url);

                InputStreamEntity reqEntity = new InputStreamEntity(
                        new FileInputStream(file), -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                //Do something with response...
                StringBuilder sbr = new StringBuilder(response.getStatusLine().getStatusCode());
                Log.d(TAG,sbr.toString());
                Log.d(TAG,"Uploaded");

            } catch (Exception e) {
                // show error
                e.printStackTrace();
            }*/

                return null;


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //getContext().unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }


    /**
     * Thread for updating the random values in the GraphView
     */
    class UpdateThread extends Thread {

        private Queue<Float> values; // Values that the view displays
        private GraphView graphView; // View to be updated
        private Random random;
        boolean execute; // Signal value for the termination of thread

        public UpdateThread(GraphView graphView, Queue<Float> values) {
            this.graphView = graphView;
            this.values = values;
            random = new Random();
        }

        // Returns the state of the values generated so far.
        public Queue<Float> getValues() {
            return values;
        }

        // Modify the signal variable so that the thread completes execution
        public void signalStop() {
            execute = false;
        }

        @Override
        public void run() {
            execute = true;
            while (execute) {

                values.add(random.nextFloat());
                values.remove();

                // In the values, maintain atmost 10 values and ane in each iteration add one
                // random values to the queue and remove one element from the queue

                float[] val = new float[values.size()];
                int i = 0;
                for (Float value : values)
                    val[i++] = value.floatValue();

                xSeries.appendData(new DataPoint(value++, random.nextFloat()), true , 10);
                //graphView.getSeries(i);
                // Invalidate the view to render again
                //graphView.postInvalidate();
                try {
                    // Sleep between different updates of the view
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
