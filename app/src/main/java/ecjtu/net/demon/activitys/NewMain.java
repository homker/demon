package ecjtu.net.demon.activitys;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import ecjtu.net.demon.DownloadService;
import ecjtu.net.demon.R;
import ecjtu.net.demon.adapter.MainAdapter;
import ecjtu.net.demon.fragment.CollageNificationFragment;
import ecjtu.net.demon.fragment.MainFragment;
import ecjtu.net.demon.fragment.TushuoFragment;
import ecjtu.net.demon.utils.ACache;
import ecjtu.net.demon.utils.HttpAsync;
import ecjtu.net.demon.utils.HttpHelper;
import ecjtu.net.demon.utils.SharedPreUtil;
import ecjtu.net.demon.utils.ToastMsg;
import ecjtu.net.demon.utils.UserEntity;
import ecjtu.net.demon.view.SlidingTabLayout;


public class NewMain extends ActionBarActivity {

    private String studentID;
    private String userName;
    private String headImage;
    private SlidingTabLayout tab;
    private ViewPager pager;
    private DrawerLayout drawerLayout;
    private View drawer;
    private String VersionUrl = "http://app.ecjtu.net/api/v1/version";
    private int duration = 300;
    private DownLoadServiceConnect downLoadServiceConnect;
    private DownLoadReceiver downLoadReceiver;
    private DownloadService downLoadService;
    private boolean isDownLoad = false;
    private String md5 = null;
    private String mSavePath;
    private static Fragment mainFragment;
    private static Fragment collageNificationFragment;
    private static Fragment tushoFragment;
    private final static String apkUrl = "http://app.ecjtu.net/download";
    private final static String rxApk = "rixin.apk";


    public static void initFragment() {
        mainFragment = new MainFragment();
        collageNificationFragment = new CollageNificationFragment();
        tushoFragment = new TushuoFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View main_view = LayoutInflater.from(this).inflate(R.layout.drawlayout, null);
        main_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        setContentView(main_view);
        //使用toolbar代替actionbar
        initFragment();
        initActionBar();
        initViewPager();
        checkVersionAsync();
        //initMainFrament();
        //initViewPager();
        //getAllConetnt(mainUrl, null);

//        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,test);
//        listView.setAdapter(arrayAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();
    }

    private void findView() {
        tab = (SlidingTabLayout) findViewById(R.id.tab);
        pager = (ViewPager) findViewById(R.id.pager);
    }


    private void initViewPager() {
        findView();
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(mainFragment);
        fragments.add(collageNificationFragment);
        fragments.add(tushoFragment);
        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(mainAdapter);
        pager.setOffscreenPageLimit(fragments.size());
        tab.setCustomTabView(R.layout.tab_style, 0);
       /* tab.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
            }

            @Override
            public int getDividerColor(int position) {
                return 0;
            }
        });*/
        tab.setViewPager(pager);
    }


    private void initUserInfo() {
        UserEntity userEntity = SharedPreUtil.getInstance().getUser();
        if (!TextUtils.isEmpty(userEntity.getStudentID())) {
            studentID = userEntity.getStudentID();
            userName = userEntity.getUserName();
            headImage = userEntity.getHeadImage();
        }
        else {
            studentID = null;
        }
    }


    public void slidingMenuClickListen(View view) {
        String url = null;
        if (!TextUtils.isEmpty(studentID)) {
            switch (view.getId()) {
                case R.id.score:
                    url = "http://score.ecjtu.net/";
                    break;
                case R.id.classquery:
                    url = "http://class.ecjtu.net/wClass.php?class=" + studentID;
                    break;
                case R.id.scran:
                    turn2ActivityWithUrl(CaptureActivity.class, null);
                    break;
                case R.id.setting:
                    turn2ActivityWithUrl(Setting.class, null);
                    break;
                default:
                    ToastMsg.builder.display("开发中...", duration);
                    //Toast.makeText(main.this, "开发中。。。", Toast.LENGTH_SHORT).show();
                /*case R.id.yktquery:;break;
                case R.id.bookquery:;break;
                case R.id.moonModel:;break;
                case R.id.setting:;break;
                */
            }
            if (url != null) {
                turn2ActivityWithUrl(ContentWebView.class, url);
            }
        } else {
            turn2ActivityWithUrl(LoginActivity.class, null);
            ToastMsg.builder.display("请先行登入", duration);
            //Toast.makeText(main.this, "请先行登入", Toast.LENGTH_SHORT).show();
        }
    }

    private void turn2ActivityWithUrl(Class activity, String url) {
        Intent intent = new Intent();
        intent.setClass(NewMain.this, activity);
        if (url != null) {
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    private void initActionBar() {
        setContentView(R.layout.activity_new_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        drawerLayout = (DrawerLayout) findViewById(R.id.DrawLayout);
        drawer = findViewById(R.id.drawer);
        toolbar.setTitle("首页");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.tool_bar_open, R.string.tool_bar_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                toolbar.setTitle("花椒助手");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        toolbar.setTitle("首页");
        invalidateOptionsMenu();
    }
};

actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        }

@Override
public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isDrawerOpen = drawerLayout.isDrawerOpen(drawer);
        menu.findItem(R.id.searchView).setVisible(!isDrawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

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
                if (!TextUtils.isEmpty(studentID)) {
                    turn2ActivityWithUrl(Setting.class,null);
                } else {
                    turn2ActivityWithUrl(LoginActivity.class,null);
                }
                ToastMsg.builder.display("更新过程中会比较卡（正在努力优化中），请稍等片刻~~", duration);
                initService();
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
                        ToastMsg.builder.display("已是最新版本，无需更新", duration);
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
                ToastMsg.builder.display("网络请求失败", duration);
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

    private void initService() {
        downLoadServiceConnect = new DownLoadServiceConnect();
//        bindService(new Intent(NewMain.this, DownloadService.class), downLoadServiceConnect, BIND_AUTO_CREATE);
        startService(new Intent(NewMain.this, DownloadService.class));
        doRegisterReceiver();
    }


    private void doRegisterReceiver() {
        if (downLoadReceiver != null) {
            downLoadReceiver = new DownLoadReceiver();
            IntentFilter intentFilter = new IntentFilter("ecjtu.net.demon.DownLoadService.isDownLoad");
            registerReceiver(downLoadReceiver, intentFilter);
        }
    }

    public final class DownLoadServiceConnect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadService = ((DownloadService.MyBinder) service).getDownLoadSercice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downLoadService = null;
        }
    }

    public class DownLoadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("tag", "收到了~！");
            isDownLoad = intent.getBooleanExtra("isDownLoad", false);
            md5 = intent.getStringExtra("md5");
            String sdpath = Environment.getExternalStorageDirectory() + "/";
            mSavePath = sdpath + "download";
            if (isDownLoad) {
                installApk(new File(mSavePath, rxApk));
            } else {
                ToastMsg.builder.display("更新失败", duration);
                DownloadByAndroid(apkUrl);
            }
        }
    }

    private void DownloadByAndroid(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void installApk(File apkfile)
    {
        if (!apkfile.exists() && md5 == HttpHelper.getFileMD5(apkfile))
        {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        this.startActivity(i);
    }

}
