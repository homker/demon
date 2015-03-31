package ecjtu.net.demon;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
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
    private ViewPager flipper;
    private int windows_width; //屏幕的宽度
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
            progressBarCircularIndeterminate.setVisibility(View.GONE);
            HashMap<String, Object> data = (HashMap<String, Object>) message.obj;
            if (newslistadapter == null) {
                newslistadapter = new Newslistadapter(main.this, data);
                newslist.setAdapter(newslistadapter);
            } else {
                newslistadapter.onDateChange(data);
            }
            newslist.setVisibility(View.VISIBLE);
            refreshLayout.setLoading(false);
            refreshLayout.setRefreshing(false);
        }
    };
    private SlidingMenu sm;
    private TextView StudentID;
    private String studentID;
    private TextView userNameView;
    private CycleImageView headIamgeView;
    private String userName;
    private String headImage;
    private String url = "http://app.ecjtu.net/api/v1/index";
    private String loginUrl = "http://user.ecjtu.net/api/login";
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

    /**
     * init view
     * 点击事件监听
     */
    private void initView() {
        newslist = (ListView) findViewById(R.id.newslist);
//        newslist.setOnScrollListener();
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
        new getNewsList(url,null).start();
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
        userNameView.setText(userName);
        headIamgeView = (CycleImageView) findViewById(R.id.UserImage);
        new updateImageThread(headIamgeView, headImage).start();

        sm.showContent();
    }



    private void turn2contentActivity(String ArticleID) {
        Intent intent = new Intent();
        intent.setClass(main.this, newscontent.class);
        Bundle bundle = new Bundle();
        bundle.putString("articleID", ArticleID);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreUtil.initSharedPreference(getApplicationContext());
        main_view = getLayoutInflater().from(this).inflate(R.layout.activity_main,null);
        main_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(main_view);

        UserEntity userEntity = SharedPreUtil.getInstance().getUser();
        if (!TextUtils.isEmpty(userEntity.getStudentID())) {
            studentID = userEntity.getStudentID();
            userName = userEntity.getUserName();
            headImage = userEntity.getHeadImage();
        }
        refreshLayout = (RefreshLayout) findViewById(R.id.fresh_layout);
        newslist = (ListView) findViewById(R.id.newslist);
        initView();
        progressBarCircularIndeterminate = (ProgressBar) findViewById(R.id.progressBarCircularIndetermininate);
        setActionBarLayout(R.layout.action_bar);
        setOverflowButtonDisplayAlways();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        windows_width = displayMetrics.widthPixels;
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


    private void initReflash(final RefreshLayout refreshLayout){
        refreshLayout.setmListView(newslist);
        refreshLayout.setColorSchemeColors(R.color.link_text_material_light);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(main.this,"加载中...",Toast.LENGTH_SHORT).show();
                new getNewsList(url,null).start();//下拉刷新时调用
            }
        });
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                newslist.removeFooterView(upToLoad);
                Log.i("tag","the count is"+newslist.getCount());
                HashMap<String,Object> hashMap = (HashMap<String, Object>) newslist.getAdapter().getItem((newslist.getCount()-3));
                String articleId = (String) hashMap.get("id");
                Log.i("tag","the articleId is "+articleId);
                new getNewsList(url,articleId).start();//向上滑动时调用
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

    /**
     * 更新图片的句柄
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

        public getNewsList(String url,String articleId) {
            this.url = url;
            this.articleId = articleId;
        }

        @Override
        public void run() {
            Message message = getNewsData.obtainMessage();
            HttpHelper httpHelper = new HttpHelper();
            message.obj = httpHelper.getNewsList(url,articleId);
            getNewsData.sendMessage(message);
        }
    }



}
