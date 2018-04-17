package edu.asu.cidse.mc.group2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {


    private WebView wv1;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wv1=(WebView)findViewById(R.id.webView);
        wv1.addJavascriptInterface(new WebAppInterface(this), "Android");
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WEBVIEW", consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });

        //    wv1.loadUrl("http://webglreport.com");
        wv1.loadUrl("file:///android_asset/index.html");
    }

}
