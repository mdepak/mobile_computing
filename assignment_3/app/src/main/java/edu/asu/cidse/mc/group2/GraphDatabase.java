package edu.asu.cidse.mc.group2;

/**
 * Created by vinoth on 3/7/18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static edu.asu.cidse.mc.group2.MainActivityFragment.getExternalStorageDirectory;

public class GraphDatabase {

    public static final String TIMESTAMP = "time";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";

    public static final String COMPONENT_NAME = "ASSIGNMENT_DATABASE";

    public static final String DBNAME = "group_2.db";
    public static final int version = 1;
    public static String create_query = "";


    private void updateCreateQuery(String tableName) {

        tableName = "sampledata";
        create_query =  "create table if not exists " +tableName+ " (id INTEGER primary key, ";

        for(int i=0; i<50; i++) create_query += " x"+i +" REAL, y"+i +" REAL, z"+i +" REAL,";

        create_query += " label INTEGER);";
    }

    private final Context context;
    public static AssignmentDatabaseHelper DBHelper;
    public static SQLiteDatabase db;

    public GraphDatabase(Context ctx, String tableName)

    {
        this.context = ctx;
        //DBHelper = new DatabaseHelper(context);
        updateCreateQuery(tableName);
        DBHelper = AssignmentDatabaseHelper.getInstance(context);

    }

    private static class AssignmentDatabaseHelper extends SQLiteOpenHelper {

        private static AssignmentDatabaseHelper sInstance;
        private static String tableName = null;

        public static synchronized AssignmentDatabaseHelper getInstance(Context context) {

            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            // See this article for more information: http://bit.ly/6LRzfx
            if (sInstance == null) {
                sInstance = new AssignmentDatabaseHelper(context.getApplicationContext());
            }
            return sInstance;
        }

        AssignmentDatabaseHelper(Context ctx) {

            //String actualPath = " /mnt/sdcard/ext_sd"+ File.separator + "Android/Data/CSE535_ASSIGNMENT2"+ File.separator + DBNAME;

            super(ctx, getExternalStorageDirectory()
                    + File.separator + "Android/Data/CSE535_ASSIGNMENT2"
                    + File.separator + DBNAME, null, version);

            /*super(ctx, " /mnt/sdcard/ext_sd"+ File.separator + "Android/Data/CSE535_ASSIGNMENT2"
                    + File.separator + DBNAME, null, version);
*/
            //super(ctx, DBNAME, null, version);
        }



        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(GraphDatabase.create_query);

                Log.d(COMPONENT_NAME, "table created");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int old, int newver) {
            Log.d(COMPONENT_NAME, "new version loadeed");
            db.execSQL("DROP TABLE IF EXISTS assign");

        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(COMPONENT_NAME,"On downgrade database called");
            db.execSQL("DROP TABLE IF EXISTS assign");
            super.onDowngrade(db, oldVersion, newVersion);

        }
    }

    public GraphDatabase open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() throws SQLException {
        DBHelper.close();

    }

    public void createTableIfNotExists()
    {
        db.execSQL(create_query);
    }


    public long insertrecords(String tableName, List<AccSample> accSampleList, Integer label) {

        tableName = "sampledata";
        ContentValues initial = new ContentValues();

        for(int i=0; i<50; i++) {
            initial.put("x"+i, accSampleList.get(i).accx);
            initial.put("y"+i, accSampleList.get(i).accy);
            initial.put("z"+i, accSampleList.get(i).accz);
        }

        initial.put("label", label);
        return db.insert(tableName, null, initial);
    }


    public Cursor getData(String tableName)
    {
        tableName = "sampledata";
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        Cursor res = null;
        if(cursor.getCount()>0) {
            res = db.rawQuery("select * from " + tableName, null);
        }
        return res;
    }


    private static float round(float val)
    {
        return (float) (Math.round(val* 100.0)/100.0);
    }


    public static List fetchRecordsForVisualization(String tableName, Context context) throws JSONException {

        List sampleList = new ArrayList<>();
        GraphDatabase graphDatabase = new GraphDatabase(context, tableName);
        graphDatabase.open();
        Cursor cursor = graphDatabase.getData(tableName);



        if (cursor != null) {
            while (cursor.moveToNext()) {


                List accSampleList = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    float x = cursor.getFloat(cursor.getColumnIndex(GraphDatabase.X + i));
                    float y = cursor.getFloat(cursor.getColumnIndex(GraphDatabase.Y + i));
                    float z = cursor.getFloat(cursor.getColumnIndex(GraphDatabase.Z + i));
                    int label = cursor.getInt(cursor.getColumnIndex("label"));

                    JSONObject point = new JSONObject();

                    point.put("x", round(x));
                    point.put("y", round(y));
                    point.put("z", round(z));
                    point.put("c", label);

                    accSampleList.add(point);
                }
                sampleList.add(accSampleList);
            }
        }
        graphDatabase.close();
        return sampleList;
    }

}
