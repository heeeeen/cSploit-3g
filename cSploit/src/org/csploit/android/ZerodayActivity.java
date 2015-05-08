package org.csploit.android;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by heeeeen on 15/5/7. @h33n
 */
public class ZerodayActivity extends ActionBarActivity{

    private EditText    m_iplist;
    private Button      m_btscan;
    private TextView    m_textRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exp_layout);
        m_iplist = (EditText)findViewById(R.id.iplist);
        m_btscan = (Button)findViewById(R.id.bdvulscan);
        m_textRes = (TextView)findViewById(R.id.bdvulresult);

        m_btscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String urlText  = m_iplist.getText().toString();

                SubnetUtils  subnet = new SubnetUtils(urlText);
                String[] ss = subnet.getInfo().getAllAddresses();
                for (String s:ss)
                    Log.d("heen", s);

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new ScanTask().execute(urlText);
                } else {
                    m_textRes.setText("No network connection available.");
                }
            }

            class ScanTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... urls) {
                    // params comes from the execute() call: params[0] is the url.
                    try {
                        return downloadUrl(urls[0]);
                    } catch (IOException e) {
                        return "Unable to retrieve web page. URL may be invalid.";
                    }

                }

                @Override
                protected void onPostExecute(String result) {
                    m_textRes.setText(result);
                }

                // Given a URL, establishes an HttpUrlConnection and retrieves
                // the web page content as a InputStream, which it returns as
                // a string.
                private String downloadUrl(String myurl) throws IOException {
                    InputStream is = null;
                    // Only display the first 500 characters of the retrieved
                    // web page content.
                    int len = 500;

                    try {
                        URL url = new URL(myurl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000 /* milliseconds */);
                        conn.setConnectTimeout(15000 /* milliseconds */);
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
        });
    }
}
