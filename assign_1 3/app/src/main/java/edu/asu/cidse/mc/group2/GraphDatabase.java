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
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class GraphDatabase {

    public static final String TIMESTAMP = "time";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";

    public static final String COMPONENT_NAME = "ASSIGNMENT_DATABASE";

    public static final String DBNAME = "group_2.db";
    public static final int version = 1;
    public static String create_query = "";


    private void updateCreateQuery(String tableName)
    {
        create_query =  "create table if not exists " +tableName+ " (time INTEGER primary key, x REAL, y REAL, z REAL);";
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

            super(ctx, Environment.getExternalStorageDirectory()
                    + File.separator + "Android/Data/CSE535_ASSIGNMENT2"
                    + File.separator + DBNAME, null, version);
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


    public long insertrecords(String tableName, long timeStamp, float x, float y, float z) {
        ContentValues initial = new ContentValues();
        initial.put(TIMESTAMP, timeStamp);
        initial.put(X, x);
        initial.put(Y, y);
        initial.put(Z, z);

        return db.insert(tableName, null, initial);
    }


    public Cursor getData(String tableName)
    {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        Cursor res = null;
        if(cursor.getCount()>0) {
            res = db.rawQuery("select * from " + tableName + " order by time DESC limit 10", null);
        }
        return res;
    }
}
