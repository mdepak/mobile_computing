package com.example.ash2277.projectapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    //https://api.nal.usda.gov/ndb/V2/reports?ndbno=01009&type=f&format=json&api_key=DEMO_KEY

    ViewPager viewpager;
    Button normalButton;
    Button thermalButton;
    Button segmentButton;
    Button startButton;
    TextView textview1;
    TextView textview2;
    TextView textview3;
    Spinner spinner;

    ArrayList<String> itemNbno = new ArrayList<>();
    ArrayList<Uri> images = new ArrayList<>();
    ArrayList<String> imagepath= new ArrayList<>();
    ArrayList<String> itemNames = new ArrayList<>();


/*

*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 23)
        {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Toast.makeText(MainActivity.this, "Permission required to write file", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        }

        viewpager = findViewById(R.id.viewPager);

        normalButton = findViewById(R.id.normalBtn);
        thermalButton = findViewById(R.id.thermalBtn);
        segmentButton = findViewById(R.id.segmentBtn);
        startButton = findViewById(R.id.startBtn);
        textview1 = findViewById(R.id.pathView1);
        textview2 = findViewById(R.id.pathView2);
        textview3 = findViewById(R.id.text3);
        spinner = findViewById(R.id.items);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!adapterView.getItemAtPosition(i).toString().equals("Choose..")) {
                    Intent segment = new Intent(MainActivity.this, ChartActivity.class);
                    segment.putExtra("Food Name", adapterView.getItemAtPosition(i).toString());
                    segment.putExtra("Food Nbno", itemNbno.get(i-1));
                    startActivity(segment);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(MainActivity.this, "Nothing seleected", Toast.LENGTH_SHORT).show();
            }
        });

        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 1);
            }
        });

        thermalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 2);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sleep(5000);
                    textview3.setVisibility(View.VISIBLE);
                    itemNames.add("Choose..");
                    itemNames.add("Soup");
                    itemNames.add("Bread");
                    itemNames.add("Sausage");
                    itemNbno.add("45045887");
                    itemNbno.add("45173035");
                    itemNbno.add("45194361");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item,itemNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Segmentation is done..!!! ", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        segmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent segment = new Intent(MainActivity.this, SegmentActivity.class);
                segment.putExtra("Normal Image",imagepath.get(0));
                startActivity(segment);
            }
        });

    }


    ///sdcard/Android/Data/CSE535_ASSIGNMENT2/group_2.db
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            Uri mImageUri=data.getData();

            String path = getPath(mImageUri);
            Log.d("Location1", "Pic's location " + path);
            images.add(mImageUri);
            imagepath.add(path);
            textview1.setText(path);
            ViewPagerAdaptor viewPagerAdapter = new ViewPagerAdaptor(this, images);
            viewpager.setAdapter(viewPagerAdapter);
        }

        else if(requestCode == 2)
        {
            Uri mImageUri=data.getData();
            String path = getPath(mImageUri);
            Log.d("Location2", "Pic's location " + path);
            images.add(mImageUri);
            imagepath.add(path);
            textview2.setText(path);
            ViewPagerAdaptor viewPagerAdapter = new ViewPagerAdaptor(this, images);
            viewpager.setAdapter(viewPagerAdapter);
        }

        Log.d("Location main", "Pic's location " + imagepath);

    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @Deprecated
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        //Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}
