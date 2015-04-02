package ecjtu.net.demon;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
    RefreshLayout refreshLayout = null;

    /**
     * 更新新闻内容的的句柄
     */
    private Handler getNewsData = new Handler() {
        public void handleMessage(Message message) {

            if(message.obj != null){
                progressBarCircularIndeterminate.setVisibility(View.GONE);
                HashMap<String, Object> data = (HashMap<String, Object>) message.obj;
                if (newslistadapter == null) {
                    Log.i("tag","第一次的apdapter");
                    newslistadapter = new Newslistadapter(main.this, data);
                    newslist.setAdapter(newslistadapter);
                } else {
                    newslistadapter.onDateChange(data);
                }
                newslist.setVisibility(View.VISIBLE);
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
            }else{
                Toast.makeText(main.this,"网络不给力呀,去抽打一下小黑吧",Toast.LENGTH_SHORT).show();
            }
            if (message.arg1 == 1){
                initReflash.onRefresh();
            }
        }
    };
    private SlidingMenu sm;
    private TextView StudentID;
    private String studentID;
    private TextView userNameView;
    private CycleImageView headIamgeView;
    private String userName;
    private String headImage;
    private NotificationCompat.Builder mBuilder;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;

    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    private final static String url = "http://app.ecjtu.net/api/v1/index";
    private final static String loginUrl = "http://user.ecjtu.net/api/login";
    private final static String apkUrl ="http://app.ecjtu.net/download";
    private final static String rxApk = "rixin.apk";
    private String md5 = null;
    private boolean isLogin = false;
    private NotificationManager mNotificationManager; //顶部通知栏的控制器
    /**
     * 更新头像的线程句柄
     */
    private Handler updateHeadImage = new Handler() {
        public void handleMessage(Message message) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) message.obj;
            Drawable drawable = (Drawable) hashMap.get("drawable");
            ImageView imageView = (ImageView) hashMap.get("imageView");
            imageView.setImageDrawable(drawable);
        }
    };

    private Handler mHandler = new Handler(){
        public void handleMessage(Message message) {
            switch (message.what)
            {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mBuilder.setProgress(100,progress,false);
                    mNotificationManager.notify(1, mBuilder.build());
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    if(md5 == getFileMD5(new File(mSavePath, rxApk))){
                        mBuilder.setContentText("更新成功~！");
                        mBuilder.setProgress(0,0,false);
                        mNotificationManager.notify(1, mBuilder.build());
                        installApk();
                    }else{
                        mBuilder.setContentText("更新失败~！");
                        mNotificationManager.notify(1, mBuilder.build());
                        Toast.makeText(main.this,"更新失败",Toast.LENGTH_SHORT).show();
                        DownloadByAndroid(apkUrl);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void DownloadByAndroid(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void installApk()
    {
        File apkfile = new File(mSavePath, rxApk);
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
                if(position != (newslist.getCount() - 1)) {
                    TextView articleIDText = (TextView) view.findViewById(R.id.articleID);
                    String articleID = (String) articleIDText.getText();
                    turn2contentActivity(articleID);
                }
            }
        });
        //初始化listView
        new getNewsList(url,null,true).start();
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
            userNameView.setText("登入修复中。。。");
        }
        headIamgeView = (CycleImageView) findViewById(R.id.UserImage);
        new updateImageThread(headIamgeView, headImage).start();

        sm.showContent();
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
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





    private void turn2contentActivity(String ArticleID) {
        String articleUrl = "http://app.ecjtu.net/api/v1/article/"+ArticleID+"/view";
        Intent intent = new Intent();
        intent.setClass(main.this, webview.class);
        Bundle bundle = new Bundle();

        bundle.putString("url", articleUrl);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initNotification()
    {
        Log.i("tag","更新开始");

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("更新中")//设置通知栏标题
                .setContentText("正在下载。。。") //设置通知栏显示内容
                .setTicker("开始更新") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.drawable.ic_launcher)
                .setProgress(100,0,false);
        mNotificationManager.notify(1, mBuilder.build());
        // 现在文件
        downloadApk();
    }

    private void downloadApk(){
        new DownLoadApkThread(apkUrl).start();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreUtil.initSharedPreference(getApplicationContext());
        main_view = getLayoutInflater().from(this).inflate(R.layout.activity_main,null);
        main_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
        initView();
        progressBarCircularIndeterminate = (ProgressBar) findViewById(R.id.progressBarCircularIndetermininate);
        setActionBarLayout(R.layout.action_bar);
        setOverflowButtonDisplayAlways();
        initReflash(refreshLayout);
        initSildingmenu(this.getBaseContext());

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

    private void setOverflowButtonDisplayAlways(){
        ViewConfiguration viewConfiguration = ViewConfiguration.get(main.this);
        try {
            Field field = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            field.setAccessible(true);
            field.setBoolean(viewConfiguration,false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private SwipeRefreshLayout.OnRefreshListener initReflash = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Toast.makeText(main.this,"加载中...",Toast.LENGTH_SHORT).show();
            new getNewsList(url,null,false).start();//下拉刷新时调用
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
                Log.i("tag","the count is"+newslist.getCount());
                HashMap<String,Object> hashMap = (HashMap<String, Object>) newslist.getAdapter().getItem((newslist.getCount()-3));
                String articleId = (String) hashMap.get("id");
                Log.i("tag","the articleId is "+articleId);
                new getNewsList(url,articleId,false).start();//向上滑动时调用
            }
        });
    }

    public void toggleMenu(View view) {
        sm.toggle();
    }

    public void slidingMenuClickListen(View view) {
        Intent intent = new Intent();
        intent.setClass(main.this, webview.class);
        Bundle bundle = new Bundle();
        String url = null;
        switch (view.getId()) {
            case R.id.score:
                url = "http://score.ecjtu.net/";
                break;
            case R.id.classquery:
                url = "http://class.ecjtu.net/wClass.php?class=" + studentID;
                break;
            case R.id.scran:
                turn2capturActivity();
                break;
            default:
                Toast.makeText(main.this, "什么鬼还在开发", Toast.LENGTH_SHORT).show();
            /*case R.id.yktquery:;break;
            case R.id.bookquery:;break;
            case R.id.moonModel:;break;
            case R.id.setting:;break;
            */
        }
        if (url != null) {
            bundle.putString("url", url);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    private void turn2capturActivity() {
        Intent intent = new Intent();
        intent.setClass(main.this, CaptureActivity.class);
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
        Log.i("tag","the main class touchEvent has been work");
        refreshLayout.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化actionbar的布局
     *
     * @param layoutID
     */
    public void setActionBarLayout(int layoutID) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layoutID, null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(view, layoutParams);
        }
        this.setTitle("你妹妹的点点");
    }

/*    */

    /**
     * 测试用
     *
     * @return
     *//*dsa
    private ArrayList<HashMap<String, Object>> getDate(){
        ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

        HashMap<String,Object>map = new HashMap<String, Object>();
        for (int i = 0; i <10 ; i++ ){
        map.put("title","first title");
        map.put("info","it's a simple info show");
        map.put("image",R.drawable.least_image);
        list.add(map);
        }
        return list;
    }*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitInBack2();
        }
        return false;
    }

    private void exitInBack2() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(main.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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


    private class DownLoadApkThread extends Thread{

        private String apkUrl;

        public DownLoadApkThread(String apkUrl){
            this.apkUrl = apkUrl;
        }


        @Override
        public void run() {
            super.run();
            // 判断SD卡是否存在，并且是否具有读写权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                // 获得存储卡的路径
                String sdpath = Environment.getExternalStorageDirectory() + "/";
                mSavePath = sdpath + "download";
                URL url = null;
                try {
                    url = new URL(apkUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                // 创建连接
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    conn.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 获取文件大小
                int length = conn.getContentLength();
                // 创建输入流
                InputStream is = null;
                try {
                    is = conn.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File file = new File(mSavePath);
                // 判断文件目录是否存在
                if (!file.exists())
                {
                    file.mkdir();
                }
                File apkFile = new File(mSavePath, rxApk);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(apkFile);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                int count = 0;
                // 缓存
                byte buf[] = new byte[1024];
                // 写入到文件中
                do
                {
                    int numread = 0;
                    try {
                        numread = is.read(buf);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    count += numread;
                    // 计算进度条位置
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWNLOAD);
                    if (numread <= 0)
                    {
                        // 下载完成
                        mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                        break;
                    }
                    // 写入文件
                    try {
                        fos.write(buf, 0, numread);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } while (!cancelUpdate);// 点击取消就停止下载.
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * 更新图片的线程
     */
    private class updateImageThread extends Thread {

        private ImageView imageView;
        private String imageUrl;

        public updateImageThread(ImageView imageView, String url) {
            this.imageView = imageView;
            this.imageUrl = url;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(imageUrl);
                Drawable ImageDrawable = Drawable.createFromStream(url.openStream(), "image");
                Message message = updateHeadImage.obtainMessage();
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("imageView", imageView);
                hashMap.put("drawable", ImageDrawable);
                message.obj = hashMap;
                updateHeadImage.sendMessage(message);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class getNewsList extends Thread {

        private String url;
        private String articleId;
        private Boolean isInit;

        public getNewsList(String url,String articleId,Boolean isInit) {
            this.url = url;
            this.articleId = articleId;
            this.isInit = isInit;
        }

        @Override
        public void run() {
            Message message = getNewsData.obtainMessage();
            HttpHelper httpHelper = new HttpHelper();
            message.obj = httpHelper.getNewsList(url,articleId,main.this,isInit);
            if(isInit){
                message.arg1 = 1;
            }else{
                message.arg1 = 0;
            }
            getNewsData.sendMessage(message);
        }
    }



}
