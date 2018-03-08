package edu.asu.cidse.mc.group2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    // View for the graph showing data

    private GraphView graphView;
    UpdateThread updateThread;
    Queue<Float> valList;

    // Horizontal axis values
    String[] horAxis= {"2700","2750","2800","2850","2900","2950","3000","3150","3200"};

    // Vertical axis values
    String[] verAxis = {"500", "1000", "1500","2000"};

    public MainActivityFragment() {
    }

    private String getTableName(String patientId, String age, String name, String sex)
    {
        return name+"_"+patientId+"_"+age+"_"+sex;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView =  inflater.inflate(R.layout.fragment_main, container, false);


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
        graphView = new GraphView(getContext(), values, "ECG readings",horAxis , verAxis, true);
        updateThread = new UpdateThread(graphView, valList);
        graphView.setBackgroundColor(Color.BLACK);
        graphView.setVisibility(View.INVISIBLE);

        rootLayout.addView(graphView);


        Button runButton = rootView.findViewById(R.id.runBtn);
        Button stopButton = rootView.findViewById(R.id.stopBtn);



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

                if (name.length() == 0)
                {         Name.setError("Name is required!");
                isValidInput = false;
                }

                if (isValidInput && (updateThread == null || !updateThread.isAlive())) {
                    updateThread =new UpdateThread(graphView, valList);
                    updateThread.start();
                    graphView.setVisibility(View.VISIBLE);

                    Intent startSenseService = new Intent(getContext(), SensorHandlerClass.class);
                    Bundle b = new Bundle();

                    //b.putString("phone", phoneNum);
                    startSenseService.putExtras(b);
                    getActivity().startService(startSenseService);

                }
            }
        });

        // OnClickListener for the Stop button click
        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            /**
             *  Handles the stop button click
             *
             *  Stops the thread by signalling and clears the view
             */
            public void onClick(View view) {
                if (updateThread != null) {
                    valList = updateThread.getValues();

                    updateThread.signalStop();
                    updateThread = null;
                    graphView.setVisibility(View.INVISIBLE);
                }
            }
        });
        return rootView;
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

                graphView.setValues(val);
                // Invalidate the view to render again
                graphView.postInvalidate();
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
