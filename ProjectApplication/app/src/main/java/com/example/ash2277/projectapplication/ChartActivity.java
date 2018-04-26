package com.example.ash2277.projectapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity{

    PieChart pieChart;
    String foodName;
    String foodNbno;

    float[] yval_soup = {(float) 2, (float) 0.7, (float) 0.471, (float) 0.278, (float) 16};
    float[] yval_sausage = {(float) 9, 20, (float) 0.644, (float) 0.144, (float) 1.5};
    float[] yval_bread = {(float) 2.7, 1, (float) 0.147, (float) 0.035, (float) 15};

    float[] yval;
    String[] xval = {"Protein","Fat","Sodium","Potassium","Carbohydrates"};

    String url = "https://api.nal.usda.gov/ndb/V2/";

    //https://api.nal.usda.gov/ndb/V2/reports?ndbno=01009&type=f&format=json&api_key=DEMO_KEY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_layout);
        Intent intent = getIntent();
        foodName = intent.getExtras().getString("Food Name");
        foodNbno = intent.getExtras().getString("Food Nbno");
        pieChart = (PieChart) findViewById(R.id.idPieChart);

        if(foodNbno.equals("45045887"))
            yval = yval_soup;
        else if(foodNbno.equals("45173035"))
            yval = yval_bread;
        else
            yval = yval_sausage;

        pieChart.setRotationEnabled(true);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(25f);
        pieChart.setCenterText(foodName);
        pieChart.setCenterTextSize(12);


        addDataSet();


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos = e.toString().indexOf("y: ");
                String val = e.toString().substring(pos+3);
                for(int i =0; i<yval.length; i++)
                {
                    if(yval[i] == Float.parseFloat(val)) {
                        pos = i;
                        break;
                    }
                }
                String label = xval[pos];
                Toast.makeText(ChartActivity.this, "Nutrition:  " + label + "\nValue:  " + val + "g"  , Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected() {
            }
        });

    }

    private void addDataSet()
    {
        ArrayList<PieEntry> yEntry = new ArrayList<>();
        ArrayList<String> xEntry = new ArrayList<>();

        for(int i=0; i<yval.length;i++)
        {
            yEntry.add(new PieEntry(yval[i],i));
        }

        for(int i=0; i<xval.length;i++)
        {
            xEntry.add(xval[i]);
        }

        PieDataSet pieDataSet = new PieDataSet(yEntry,"Nutrition values");
        pieDataSet.setSliceSpace(1);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.CYAN);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.MAGENTA);
        colors.add(Color.GRAY);
        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);


        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }


}



