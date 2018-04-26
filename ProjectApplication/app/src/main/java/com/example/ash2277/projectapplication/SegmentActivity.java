package com.example.ash2277.projectapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class SegmentActivity extends AppCompatActivity {

    ImageView imgview;
    ViewPager viewpager;

    ArrayList<Uri> images = new ArrayList<>();
    ArrayList<String> image_path = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segmented_layout);
        imgview = findViewById(R.id.segImgView);
        viewpager = findViewById(R.id.segViewPager);

        image_path.add("/storage/emulated/0/soup.jpg");
        image_path.add("/storage/emulated/0/bread.bmp");
        image_path.add("/storage/emulated/0/sauage.jpg");

        Intent seg = getIntent();
        String path = seg.getStringExtra("Normal Image");
        File imgFile = new  File(path);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgview.setImageBitmap(myBitmap);

        }

        for(int i = 0;i<image_path.size(); i++)
        {
            images.add(Uri.fromFile(new File(image_path.get(i))));
        }


        ViewPagerAdaptor viewPagerAdapter = new ViewPagerAdaptor(this, images);
        viewpager.setAdapter(viewPagerAdapter);
    }
}
