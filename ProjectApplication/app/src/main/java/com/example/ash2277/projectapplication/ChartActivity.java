package com.example.ash2277.projectapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
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

    float[] yval = {25,35,15,5,50};
    String[] xval = {"Protein","Fiber","Fat","Vitamins","Calories"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_layout);
        Intent intent = getIntent();
        foodName = intent.getExtras().getString("Food Name");
        pieChart = (PieChart) findViewById(R.id.idPieChart);

        pieChart.setRotationEnabled(true);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setHoleRadius(25f);
        pieChart.setCenterText(foodName);
        pieChart.setCenterTextSize(15);

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
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
