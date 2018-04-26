package com.example.ash2277.projectapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ListView listview = (ListView) findViewById(R.id.list_item);

        String[] values = new String[]{"2018/04/25", "2018/04/25", "2018/04/25","2018/04/25","2018/04/25","2018/04/25", "2018/04/25", "2018/04/25"};


        final ArrayList<String> list = new ArrayList();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                values);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }

        });
    }


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        private final Context context;
        private final String[] values;
        private final String[] filePaths;


        public StableArrayAdapter(Context context, String[] values) {
            super(context, R.layout.list_layout, values);
            this.context = context;
            this.values = values;
            filePaths = new String[values.length];
            filePaths[0] = "/storage/emulated/0/Download/10_19_color.jpg";
            filePaths[1] = "/storage/emulated/0/Download/2_15_color.jpg";
            filePaths[2] = "/storage/emulated/0/Download/2_15_color.jpg";
            filePaths[3] = "/storage/emulated/0/Download/34_9_color.jpg";
            filePaths[4] = "/storage/emulated/0/Download/1_17_color.jpg";
            filePaths[5] = "/storage/emulated/0/Download/6_7_color.jpg";
            filePaths[6] = "/storage/emulated/0/Download/40_19_color.jpg";
            filePaths[7] = "/storage/emulated/0/Download/37_8_color.jpg";

        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            File imgFile = new File(filePaths[position]);

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
            textView.setText(values[position]);
            // change the icon for Windows and iPhone

            String s = values[position];

            return rowView;

        }
    }

}

