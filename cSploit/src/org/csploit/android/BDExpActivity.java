package org.csploit.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.csploit.android.bdcmdlist.AddContactInfo;
import org.csploit.android.bdcmdlist.Downloadfile;
import org.csploit.android.bdcmdlist.Geolocation;
import org.csploit.android.bdcmdlist.GetApn;
import org.csploit.android.bdcmdlist.GetApplist;
import org.csploit.android.bdcmdlist.GetCuid;
import org.csploit.android.bdcmdlist.GetLocString;
import org.csploit.android.bdcmdlist.GetSearchboxInfo;
import org.csploit.android.bdcmdlist.GetServiceInfo;
import org.csploit.android.bdcmdlist.SendIntent;
import org.csploit.android.bdcmdlist.Uploadfile;

import java.util.ArrayList;

/**
 * Created by heeeeen on 15/5/19. @h33n
 * UI to initiate Exp commands
 */
public class BDExpActivity extends ActionBarActivity {

    private ListView mCmdlist = null;
    private ArrayList<String> mStrCmdlist = null;

    private String[]  mCmdNames = {"geolocation", "getsearchboxinfo", "getapn", "getserviceinfo",
            "getpackageinfo", "sendintent", "getcuid", "getlocstring",
            "scandownloadfile", "addcontactinfo", "getapplist", "downloadfile", "uploadfile"};

    private String[] mCmdDescriptions = {"获取目标经纬度","获取目标搜索应用的信息","获取目标手机apn信息",
            "获取服务信息","获取安装包信息","发送intent对象","获取目标手机IMEI","获取目标地址位置","扫描下载的文件",
            "给目标手机添加联系人","获取目标手机所有应用列表","让目标手机下载应用进行安装","上传应用让目标手机安装"};

    private int[] mImageIds = {R.drawable.action_back, R.drawable.action_exploit, R.drawable.action_forge,
            R.drawable.action_forward,R.drawable.action_hijack,R.drawable.action_image,
            R.drawable.action_injection, R.drawable.action_inspect, R.drawable.action_kill,
            R.drawable.action_login, R.drawable.action_mitm, R.drawable.action_monitor,
            R.drawable.action_open};

    private String mTargetPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePrefs = getSharedPreferences("THEME", 0);
        Boolean isDark = themePrefs.getBoolean("isDark", false);

        if (isDark)
            setTheme(R.style.DarkTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.actions_layout);
        mCmdlist = (ListView) findViewById(R.id.android_list);

        mStrCmdlist = new ArrayList<String>();
        for(int i=0; i < mCmdNames.length; i++){
            mStrCmdlist.add(mCmdNames[i]);
        }
        BdActionsAdapter mCmdlistAdapter = new BdActionsAdapter(mStrCmdlist);
        mCmdlist.setAdapter(mCmdlistAdapter);

        mTargetPhone = getIntent().getStringExtra("targetIP");
        Log.d("heen", "target mobile phone is " + mTargetPhone);

        setTitle("Expl0it >"+mTargetPhone);

        mCmdlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:    //geolocation
                        Intent i0 = new Intent();
                        i0.setComponent(new ComponentName(BDExpActivity.this,Geolocation.class));
                        i0.putExtra("targetIP", mTargetPhone);
                        startActivity(i0);
                        break;
                    case 1:     //getsearchboxinfo
                        Intent i1 = new Intent();
                        i1.setComponent(new ComponentName(BDExpActivity.this, GetSearchboxInfo.class));
                        i1.putExtra("targetIP", mTargetPhone);
                        startActivity(i1);
                        break;

                    case 2:    //getapn
                        Intent i2 = new Intent();
                        i2.setComponent(new ComponentName(BDExpActivity.this,GetApn.class));
                        i2.putExtra("targetIP", mTargetPhone);
                        startActivity(i2);
                        break;

                    case 3:     //getserviceinfo
                        Intent i3 = new Intent();
                        i3.setComponent(new ComponentName(BDExpActivity.this, GetServiceInfo.class));
                        i3.putExtra("targetIP", mTargetPhone);
                        startActivity(i3);
                        break;

                    case 5:     //sendintent
                        Intent i5 = new Intent();
                        i5.setComponent(new ComponentName(BDExpActivity.this, SendIntent.class));
                        i5.putExtra("targetIP", mTargetPhone);
                        startActivity(i5);
                        break;
                    case 6:     //getcuid
                        Intent i6 = new Intent();
                        i6.setComponent(new ComponentName(BDExpActivity.this, GetCuid.class));
                        i6.putExtra("targetIP", mTargetPhone);
                        startActivity(i6);
                        break;
                    case 7:     //getlocstring
                        Intent i7 = new Intent();
                        i7.setComponent(new ComponentName(BDExpActivity.this, GetLocString.class));
                        i7.putExtra("targetIP", mTargetPhone);
                        startActivity(i7);
                        break;

                    case 9:     //addcontactinfo
                        Intent i9 = new Intent();
                        i9.setComponent(new ComponentName(BDExpActivity.this,AddContactInfo.class));
                        i9.putExtra("targetIP", mTargetPhone);
                        startActivity(i9);
                        break;
                    case 10:     //getapplist
                        Intent i10 = new Intent();
                        i10.setComponent(new ComponentName(BDExpActivity.this,GetApplist.class));
                        i10.putExtra("targetIP", mTargetPhone);
                        startActivity(i10);
                        break;
                    case 11:     //downloadfile
                        Intent i11 = new Intent();
                        i11.setComponent(new ComponentName(BDExpActivity.this,Downloadfile.class));
                        i11.putExtra("targetIP", mTargetPhone);
                        startActivity(i11);
                        break;
                    case 12:     //uploadfile
                        Intent i12 = new Intent();
                        i12.setComponent(new ComponentName(BDExpActivity.this,Uploadfile.class));
                        i12.putExtra("targetIP", mTargetPhone);
                        startActivity(i12);
                        break;
                }
            }
        });

    }

    public class BdActionsAdapter extends ArrayAdapter<String> {
        public BdActionsAdapter(ArrayList<String> al) {
            super(BDExpActivity.this, R.layout.bd_actions_list_item, al);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            BdCmdHolder holder;

            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) BDExpActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.bd_actions_list_item, parent, false);
                holder = new BdCmdHolder();

                holder.icon = (ImageView) (row != null ? row.findViewById(R.id.actionIcon) : null);
                holder.name = (TextView) (row != null ? row.findViewById(R.id.actionName) : null);
                holder.description = (TextView) (row != null ? row.findViewById(R.id.actionDescription) : null);
                if (row != null) row.setTag(holder);

            } else holder = (BdCmdHolder) row.getTag();


            holder.icon.setImageResource(mImageIds[position]);
            holder.name.setText(mStrCmdlist.get(position));
            holder.description.setText(mCmdDescriptions[position]);

            return row;
        }
        public class BdCmdHolder {
            ImageView icon;
            TextView name;
            TextView description;
        }
    }
}
