package edu.asu.cidse.mc.group2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static edu.asu.cidse.mc.group2.GraphDatabase.DBNAME;

/**
 * A placeholder fragment containing a simple view. *
 **/
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

    String table_name = null;

    private static final File EXTERNAL_STORAGE_DIRECTORY
            = getDirectory("EXTERNAL_STORAGE", "/sdcard");

    static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }

    public static File getExternalStorageDirectory() {
        return EXTERNAL_STORAGE_DIRECTORY;
    }

    public static final String TABLE_NAME = "table_name";
    public static final String LABEL = "label";

    // Horizontal axis values
    String[] horAxis = {"2700", "2750", "2800", "2850", "2900", "2950", "3000", "3150", "3200"};

    // Vertical axis values
    String[] verAxis = {"500", "1000", "1500", "2000"};

    String path = getExternalStorageDirectory() +
            File.separator + "Android/Data/CSE535_ASSIGNMENT2" +
            File.separator + DBNAME;


    String url = "http://impact.asu.edu/";

    // We have used the Android GraphView Library - Added using Gradle.

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url).build();


    private void resetGraph()
    {
        xSeries.resetData(new DataPoint[]{});
        ySeries.resetData(new DataPoint[]{});
        zSeries.resetData(new DataPoint[]{});
    }

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
                String str = bundle.getString(SensorHandlerClass.HIDE);

                if(str.equals("false")) {
                    updateGraph(accX, accY, accZ);
                    Log.d(TAG, "Recieved data in message " + accX);
                }
                else
                    graphView.setVisibility(View.INVISIBLE);


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
        Button trainButton = rootView.findViewById(R.id.trainBtn);

        // OnClickListener for the upload button
        trainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<Sample> accSamples = fetchRecordsForTraining("sampledata");
                TrainSVM trainSVM = new TrainSVM();
                String[] modelParams = new String[1];

                try {
                    trainSVM.run(accSamples,modelParams );
                } catch (IOException e) {
                    System.out.println("Exception ");
                    e.printStackTrace();
                }
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

                Spinner sp = (Spinner)rootView.findViewById(R.id.spinner);
                int spinner_pos = sp.getSelectedItemPosition();
                Log.d(TAG, "Value of the Spinner chosen : "+ spinner_pos);
                boolean isValidInput = true;

                table_name = "sampledata";

                if (isValidInput && (updateThread == null || !updateThread.isAlive())) {
                    updateThread = new UpdateThread(graphView, valList);
                    //updateThread.start();
                    graphView.setVisibility(View.VISIBLE);

                    Intent sensorService = new Intent(getContext(), SensorHandlerClass.class);
                    Bundle b = new Bundle();
                    b.putString(TABLE_NAME, "sampledata");
                    b.putInt(LABEL, spinner_pos);
                    sensorService.putExtras(b);
                    getActivity().startService(sensorService);
                }
            }
        });

        return rootView;
    }

    private List<Sample> fetchRecordsForTraining(String tableName)
    {

        List<Sample> sampleList = new ArrayList<>();
        GraphDatabase graphDatabase = new GraphDatabase(getContext(), tableName);
        graphDatabase.open();
        Cursor cursor = graphDatabase.getData(tableName);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                List<AccSample> accSampleList = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    float x = cursor.getFloat(cursor.getColumnIndex(GraphDatabase.X + i));
                    float y = cursor.getFloat(cursor.getColumnIndex(GraphDatabase.Y + i));
                    float z = cursor.getFloat(cursor.getColumnIndex(GraphDatabase.Z + i));
                    AccSample accSample = new AccSample(x, y, z);
                    accSampleList.add(accSample);
                }
                Sample sample = new Sample(accSampleList, cursor.getInt(cursor.getColumnIndex("label")));
                sampleList.add(sample);
            }
        }
        graphDatabase.close();
        return sampleList;
    }

    private void fetchRecordsFromDataBase(String tableName)
    {
        GraphDatabase graphDatabase = new GraphDatabase(getContext(), tableName);
        graphDatabase.open();
        Cursor cursor = graphDatabase.getData(tableName);

        if (cursor != null)
        {while (cursor.moveToNext()) {
           float x =  cursor.getFloat(cursor.getColumnIndex(GraphDatabase.X));
            float y = cursor.getFloat(cursor.getColumnIndex(GraphDatabase.Y));
            float z = cursor.getFloat(cursor.getColumnIndex(GraphDatabase.Z));

            graphView.setVisibility(View.VISIBLE);
            updateGraph(x, y, z);

            Log.d(TAG, "Values from db : "+ x + "\t" + y+ "\t"+z);
        }
        }
        else
        {
            resetGraph();
        }
        graphDatabase.close();
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
            }
        }
    }
}
