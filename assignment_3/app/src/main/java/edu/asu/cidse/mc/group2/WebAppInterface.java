package edu.asu.cidse.mc.group2;

/**
 * Created by student on 4/11/18.
 */


import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class WebAppInterface {
    Context mContext;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public String getData() {
        ArrayList data = null;
        try {
            ArrayList temp = (ArrayList) GraphDatabase.fetchRecordsForVisualization("dummyname", mContext);

            data = new ArrayList();
            for(int idx =0 ; idx< temp.size(); idx++)
            {
                data.add(temp.get(idx));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("WebAppInterface", data.toString());
        return data.toString();
    }
}