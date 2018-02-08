package edu.asu.cidse.mc.healthmonitor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Main2Activity extends AppCompatActivity {

    private GraphView graphView;
    UpdateThread updateThread;
    Queue<Float> valList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        float[] values = new float[10];
        valList = new LinkedList();
        for(int i=0;i<10; i++) {
            float val = new Random().nextFloat();
            values[i] = val;
            valList.add(val);
        }


        RelativeLayout rootLayout =  findViewById(R.id.layout);

        graphView = new GraphView(this, values, "ECG readigns", new String[]{"sdafsd"}, new String[]{"sdfasdf"}, true);
        updateThread = new UpdateThread(graphView, valList);
        //updateThread.start();

        rootLayout.addView(graphView);
        graphView.getLayoutParams().height = 250;
    }


    public void runButtonClicked(final View v)
    {
        if(updateThread== null || !updateThread.isAlive())
        {
            updateThread = new UpdateThread(graphView, valList);
            updateThread.start();
        }
    }

    public void stopButtonClicked(final View v) {
        if (updateThread != null) {
            valList = updateThread.getValues();
            //updateThread.interrupt();
            updateThread.signalStop();
            updateThread = null;
        }
    }


    class UpdateThread extends Thread{

        private Queue<Float> values;
        private GraphView graphView;
        private Random random;
        boolean execute;

        public UpdateThread(GraphView graphView, Queue<Float> values)
        {
            this.graphView = graphView;
            this.values = values;
            random = new Random();

        }

        public Queue<Float> getValues()
        {
            return values;
        }

        public void signalStop()
        {
         execute = false;
        }

        @Override
        public void run() {
                execute = true;
                while(execute) {
                   // Toast.makeText(this, "isInteruupted")
                    values.add(random.nextFloat());
                    values.remove();

                    float[] val = new float[values.size()];
                    int i=0;
                    for(Float value : values)
                        val[i++] = value.floatValue();

                    graphView.setValues(val);
                    graphView.postInvalidate();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

        }
    }
}
