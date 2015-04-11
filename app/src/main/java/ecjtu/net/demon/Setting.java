package ecjtu.net.demon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecjtu.net.demon.view.CycleImageView;

/**
 * Created by 圣麟 on 2015/3/30.
 */
public class Setting extends Activity {

    private ListView userListView;
    private ListView aboutListView;
    private CycleImageView headImage;
    private SimpleAdapter listAdapter;
    private UserEntity userEntity;
    private String md5;
    private Button exit;
    private String VersionUrl = "http://app.ecjtu.net/api/v1/version";
    private int duration = 300;


    private void showNoticeDialog()
    {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        // 更新
        builder.setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                // 显示下载对话框
                downloadApk();
            }
        });
        // 稍后更新
        builder.setNegativeButton(R.string.soft_update_later, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        AlertDialog noticeDialog = builder.create();
        noticeDialog.show();
    }
    private void downloadApk(){
        Bundle bundle = new Bundle();
        bundle.putString("update", md5);
        turn2mianActivity(bundle);
    }
    private void turn2mianActivity(Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(this, main.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        userEntity = SharedPreUtil.getInstance().getUser();
        exit = (Button) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreUtil.getInstance().DeleteUser();
                turn2mianActivity(null);
            }
        });
        getActionBar().setDisplayHomeAsUpEnabled(true);
        userListView = (ListView) findViewById(R.id.userlist);
        SimpleAdapter userAdapter = new SimpleAdapter(this, getUserData(), R.layout.list_item, new String[] { "notes","information" }, new int[] { R.id.notes, R.id.information});
        userListView.setAdapter(userAdapter);
        headImage = (CycleImageView) findViewById(R.id.imageView2);
        aboutListView = (ListView) findViewById(R.id.aboutlist);
        SimpleAdapter aboutAdapter = null;
        try {
            aboutAdapter = new SimpleAdapter(this, getAboutData(), R.layout.list_item, new String[] { "notes","information" }, new int[] { R.id.notes, R.id.information});
        } catch (Exception e) {
            e.printStackTrace();
        }
        aboutListView.setAdapter(aboutAdapter);
        aboutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        checkVersionAsync();
                        break;
                }
            }
        });
    }

    private void checkVersionAsync(){
        HttpAsync.get(VersionUrl, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.i("tag", "it start");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int versionCode = response.getInt("version_code");
                    md5 = response.getString("md5");
                    if (versionCode > getVersionCode()) {
                        Log.i("tag", "需要更新");
                        showNoticeDialog();
                    } else {
                        Log.i("tag", "我们不需要更新");
                        ToastMsg.builder.display("已是最新版本，无需更新",duration);
                        //Toast.makeText(Setting.this,"已是最新版本，无需更新",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastMsg.builder.display("网络请求失败",duration);
                //Toast.makeText(Setting.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }

        });
    }
    private int getVersionCode() throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionCode;
    }


    private List<Map<String, Object>> getUserData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("notes", "账号信息");
        map.put("information",getUserId());
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("notes", "昵称");
        map.put("information",getNickname());
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("notes", "密码");
        map.put("information","******");
        list.add(map);
        return list;
    }

    private List<Map<String, Object>> getAboutData() throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("notes", "消息通知");
        map.put("information",">");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("notes", "版本信息");
        map.put("information",getVersionName());
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("notes", "关于我们");
        map.put("information",">");
        list.add(map);
        return list;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(Setting.this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    Log.i("tag", "nihao" + String.valueOf(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getVersionName() throws Exception
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }

    public String getUserId() {

        return userEntity.getStudentID();
    }

    public String getNickname() {
        return userEntity.getUserName();
    }
}
