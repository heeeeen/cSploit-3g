package org.csploit.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
