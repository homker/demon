package ecjtu.net.demon;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.datatype.Duration;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import ecjtu.net.demon.view.CycleImageView;
import ecjtu.net.demon.view.RefreshLayout;


public class main extends InstrumentedActivity {

    private static boolean isExit = false;
    private ListView newslist;
    private Newslistadapter newslistadapter;
    private ProgressBar progressBarCircularIndeterminate;
    private View main_view;
    private TextView upToLoad;
    private RefreshLayout refreshLayout = null;
    private SlidingMenu sm;
    private TextView StudentID;
    private String studentID;
    private TextView userNameView;
    private CycleImageView headIamgeView;
    private String userName;
    private String headImage;
    private NotificationCompat.Builder mBuilder;
    private String mSavePath;
    private final static String url = "http://app.ecjtu.net/api/v1/index";
    private final static String apkUrl ="http://app.ecjtu.net/download";
    private final static String rxApk = "rixin.apk";
    private String md5 = null;
    private boolean isLogin = false;
    private NotificationManager mNotificationManager; //顶部通知栏的控制器
    private DisplayImageOptions options;
    private int duration = 200;

    private void DownloadByAndroid(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void installApk(File apkfile)
    {
        if (!apkfile.exists())
        {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        this.startActivity(i);
    }

    /**
     * init view
     * 点击事件监听
     */
    private void initView() {
        newslist = (ListView) findViewById(R.id.newslist);
        upToLoad = new TextView(main.this);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        upToLoad.setLayoutParams(layoutParams);
        upToLoad.setGravity(Gravity.CENTER);
        upToLoad.setText("向上滑动加载更多");
        newslist.addFooterView(upToLoad);
        newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView articleIDText = (TextView) view.findViewById(R.id.articleID);
                String articleID = (String) articleIDText.getText();
                String articleUrl = "http://app.ecjtu.net/api/v1/article/" + articleID + "/view";
                main.this.turn2Activity(webview.class, articleUrl);
            }
        });
        //初始化listView
        setNewslist(url, null, true);
    }

    /**
     * 初始化silidingmenu
     */
    private void initSildingmenu(Context context) {
        sm = new SlidingMenu(context);
        sm.setBehindOffsetRes(R.dimen.sling_margin_main);
        sm.setFadeEnabled(true);
        sm.setMode(SlidingMenu.LEFT);
        sm.setBehindScrollScale(0);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        sm.setShadowWidthRes(R.dimen.shawdow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        sm.setMenu(R.layout.left_menu);
        StudentID = (TextView) findViewById(R.id.UserID);
        StudentID.setText(studentID);
        userNameView = (TextView) findViewById(R.id.UserName);
        if (userName != null){
            userNameView.setText(userName);
        }else{
            userNameView.setText("点击登入");
            userNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(main.this, LoginActivity.class);
                    main.this.startActivity(intent);
                }
            });
        }
        headIamgeView = (CycleImageView) findViewById(R.id.UserImage);
        headIamgeView.setImageResource(R.drawable.userimage);
        if (headImage == null){
            headImage = "http://img5.imgtn.bdimg.com/it/u=37747847,1258561098&fm=21&gp=0.jpg";
        }
        ImageLoader.getInstance().displayImage(headImage, headIamgeView, options);
        sm.showContent();
    }

   /* public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
*/

    private void initNotification()
    {
        Log.i("tag", "更新开始");

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("更新中")//设置通知栏标题
                .setContentText("正在下载。。。") //设置通知栏显示内容
                .setTicker("开始更新") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_LIGHTS)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.drawable.logo_cycle)
                .setProgress(100, 0, false);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(1, notification);
        // 现在文件
        DownLoadApk();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreUtil.initSharedPreference(getApplicationContext());
        main_view = LayoutInflater.from(this).inflate(R.layout.activity_main,null);
        main_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        setContentView(main_view);

        Intent intent = getIntent();
         md5 = intent.getStringExtra("update");
        if ( md5 != null){
            initNotification();
        }

        UserEntity userEntity = SharedPreUtil.getInstance().getUser();
        if (!TextUtils.isEmpty(userEntity.getStudentID())) {
            studentID = userEntity.getStudentID();
            userName = userEntity.getUserName();
            headImage = userEntity.getHeadImage();
            isLogin = true;
        }

        refreshLayout = (RefreshLayout) findViewById(R.id.fresh_layout);
        newslist = (ListView) findViewById(R.id.newslist);
        progressBarCircularIndeterminate = (ProgressBar) findViewById(R.id.progressBarCircularIndetermininate);

        initImageloader();
        initView();
        setActionBarLayout(R.layout.action_bar);
        setOverflowButtonDisplayAlways();
        initReflash(refreshLayout);
        initSildingmenu(this.getBaseContext());


    }

    private void initImageloader(){
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(main.this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.userimage)
                .showImageOnFail(R.drawable.userimage)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setOverflowButtonDisplayAlways(){
        ViewConfiguration viewConfiguration = ViewConfiguration.get(main.this);
        try {
            Field field = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            field.setAccessible(true);
            field.setBoolean(viewConfiguration,false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener initReflash = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            main.this.setNewslist(url, null, false);
        }
    };


    private void initReflash(final RefreshLayout refreshLayout){
        refreshLayout.setmListView(newslist);
        refreshLayout.setColorSchemeColors(R.color.link_text_material_light);
        refreshLayout.setOnRefreshListener(initReflash);
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                newslist.removeFooterView(upToLoad);
                Log.i("tag", "the count is" + newslist.getCount());
                HashMap<String, Object> hashMap = (HashMap<String, Object>) newslist.getAdapter().getItem((newslist.getCount() - 3));
                String articleId = String.valueOf(hashMap.get("id"));
                Log.i("tag", "the articleId is " + articleId);
                main.this.setNewslist(url, articleId, false);
            }
        });
    }

    public void toggleMenu(View view) {
        sm.toggle();
    }

    public void slidingMenuClickListen(View view) {
        String url = null;
        if(studentID != null){
            switch (view.getId()) {
                case R.id.score:
                    url = "http://score.ecjtu.net/";
                    break;
                case R.id.classquery:
                    url = "http://class.ecjtu.net/wClass.php?class=" + studentID;
                    break;
                case R.id.scran:
                    turn2Activity(CaptureActivity.class,null);
                    break;
                case R.id.setting:
                    turn2Activity(Setting.class,null);
                    break;
                default:
                    ToastMsg.builder.display("开发中...",duration);
                    //Toast.makeText(main.this, "开发中。。。", Toast.LENGTH_SHORT).show();
                /*case R.id.yktquery:;break;
                case R.id.bookquery:;break;
                case R.id.moonModel:;break;
                case R.id.setting:;break;
                */
            }
            if (url != null) {
                turn2Activity(webview.class,url);
            }
        }else{
            turn2Activity(LoginActivity.class,null);
            ToastMsg.builder.display("请先行登入",duration);
            //Toast.makeText(main.this, "请先行登入", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                sm.toggle();
                //
                break;
            default:return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void turn2Activity(Class activity,String url) {
        Intent intent = new Intent();
        intent.setClass(main.this, activity);
        if (url != null){
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("tag", "the main class touchEvent has been work");
        refreshLayout.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent( MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化actionbar的布局
     *
     * @param layoutID 布局
     */
    public void setActionBarLayout(int layoutID) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
//            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setDisplayShowHomeEnabled(false);
//            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setLogo(R.drawable.sliderbutton);
            actionBar.setTitle("新闻");
//            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
//            View view = inflater.inflate(layoutID, null);
//            TextView title = (TextView) view.findViewById(R.id.title);
//            title.setText("新闻");
/*            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);*/
//            actionBar.setCustomView(view);
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitInBack2();
        }
        return false;
    }

    private void exitInBack2() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            ToastMsg.builder.display("再按一次退出程序",duration);
            //Toast.makeText(main.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    private void setNewslist(String url,final String lastId ,Boolean isInit){
        final HashMap<String, Object> list = new HashMap<>();
        if(lastId != null){
            url = url + "?until=" + lastId;
        }
        Log.i("tag","请求链接："+url);
        final ACache newsListCache = ACache.get(main.this);
        JSONObject cache = newsListCache.getAsJSONObject("newsList");
        if(cache != null){//判断缓存是否为空
            Log.i("tag", "我们使用了缓存~！");
            try {
                JSONObject slide_article = cache.getJSONObject("slide_article");
                JSONArray slide_articles = slide_article.getJSONArray("articles");
                JSONObject normal_article = cache.getJSONObject("normal_article");
                JSONArray normal_articles = normal_article.getJSONArray("articles");
                list.put("slide_articles",jsonArray2Arraylist(slide_articles));
                list.put("normal_articles",jsonArray2Arraylist(normal_articles));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (newslistadapter == null) {
                newslistadapter = new Newslistadapter(main.this, list);
                newslist.setAdapter(newslistadapter);
            }else{
                newslistadapter.onDateChange(list);
            }
            progressBarCircularIndeterminate.setVisibility(View.GONE);//影藏进度条，显示listview
            newslist.setVisibility(View.VISIBLE);
        }
        HttpAsync.get(url,new JsonHttpResponseHandler(){
            @Override
            public void onStart() {
                ToastMsg.builder.display("正在加载...",duration);
                //Toast.makeText(main.this,"正在加载。。。",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (lastId == null){//只缓存最新的内容列表
                    newsListCache.remove("newsList");
                    newsListCache.put("newsList",response,7*ACache.TIME_DAY);
                }
                try{
                    JSONObject slide_article = response.getJSONObject("slide_article");
                    JSONArray slide_articles = slide_article.getJSONArray("articles");
                    JSONObject normal_article = response.getJSONObject("normal_article");
                    JSONArray normal_articles = normal_article.getJSONArray("articles");
                    list.put("slide_articles",jsonArray2Arraylist(slide_articles));
                    list.put("normal_articles", jsonArray2Arraylist(normal_articles));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("tag", "更新线程执行成功");
                if (newslistadapter != null){
                    newslistadapter.onDateChange(list);
                }else{
                    newslistadapter = new Newslistadapter(main.this,list);
                    newslist.setAdapter(newslistadapter);
                }
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ToastMsg.builder.display("网络环境好像不是很好呀~！",duration);
                //Toast.makeText(main.this,"网络环境好像不是很好呀~！",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                progressBarCircularIndeterminate.setVisibility(View.GONE);//影藏进度条，显示listview
                newslist.setVisibility(View.VISIBLE);
            }
        });
    }
    /**
     * 将json数组变成arraylist
     * @param jsonArray 输入你转换的jsonArray
     * @return 返回arraylist
     */
    private ArrayList<HashMap<String,Object>> jsonArray2Arraylist(JSONArray jsonArray){
        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<>();
        for (int i = 0; i< jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String,Object> item = new HashMap<>();
                item.put("id",jsonObject.getInt("id"));
                item.put("title",jsonObject.getString("title"));
                item.put("updated_at",jsonObject.getString("updated_at"));
                item.put("info",jsonObject.getString("info"));
                String imageUrl = "http://app.ecjtu.net"+jsonObject.getString("thumb");
                item.put("thumb",imageUrl);
                arrayList.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    private void DownLoadApk(){
        HttpAsync.get(apkUrl, new FileAsyncHttpResponseHandler(main.this) {
            @Override
            public void onStart() {
                Log.i("tag","下载开始");
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                mBuilder.setContentText("更新失败~！");
                mNotificationManager.notify(1, mBuilder.build());
                ToastMsg.builder.display("更新失败",duration);
                //Toast.makeText(main.this,"更新失败",Toast.LENGTH_SHORT).show();
                DownloadByAndroid(apkUrl);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                int progress = (int) (((float) bytesWritten / totalSize) * 100);
                mBuilder.setProgress(100, progress, false);
                mNotificationManager.notify(1, mBuilder.build());
            }

            @Override
            public void onSuccess(int i, Header[] headers, File file) {
                String sdpath = Environment.getExternalStorageDirectory() + "/";
                mSavePath = sdpath + "download";
                File apkfile = new File(mSavePath,rxApk);
                file.renameTo(apkfile);
                // 安装文件
                    mBuilder.setContentText("更新成功~！");
                    mBuilder.setProgress(0,0,false);
                    mNotificationManager.notify(1, mBuilder.build());
                    installApk(apkfile);
            }
        });
    }
}
