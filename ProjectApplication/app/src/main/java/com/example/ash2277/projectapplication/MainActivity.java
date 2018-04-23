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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

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

public class MainActivity extends AppCompatActivity {

    String url = "http://impact.asu.edu/";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url).build();

    Api api = retrofit.create(Api.class);

    ImageView imageView;
    private String imagepath=null;

    public interface  Api{
        @Multipart
        @POST("CSE535Spring18Folder/UploadToServer.php")
        Call<ResponseBody> uploadFile (@Part MultipartBody.Part file, @Part("desc") RequestBody desc);

        @GET("CSE535Spring18Folder/{filename}")
        Call<ResponseBody> downloadFile(@Path("filename") String filename);

    }

/*
    <android.support.v4.view.ViewPager
    android:id="@+id/viewPager"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:layout_alignParentStart="true"
    android:layout_centerVertical="true">
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


        imageView = (ImageView) findViewById(R.id.imgView);

        Button normalButton = findViewById(R.id.normalBtn);
        Button thermalButton = findViewById(R.id.thermalBtn);
        Button segmentButton = findViewById(R.id.segmentBtn);
        Button startButton = findViewById(R.id.startBtn);

        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 1);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1) {
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            ///sdcard/Android/Data/CSE535_ASSIGNMENT2/group_2.db
            File file = new File(imagepath);

            if(file.exists())
                Log.d("Deep", "File found in the SD card..");
            else
                Log.d("Deep", "File not found in the SD card..");


            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
            String tableName = "tableName";
            RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), tableName);

            Call<ResponseBody> call = api.uploadFile(body,descBody);

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, retrofit2.Response response) {
                    Log.d("Upload", "File uploaded");
                    Log.d("Response value", response.raw().toString());
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.d("Upload", "File not uploaded");
                }
            });
            imageView.setImageURI(selectedImageUri);
        }

    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @Deprecated
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }




}
