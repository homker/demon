package ecjtu.net.demon;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.InputStream;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;


public class New_login extends ActionBarActivity {

    private RelativeLayout background ;
    private ImageView site;
    private ImageView bottomLogo;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private String loginUrl = "http://user.ecjtu.net/api/";
    private String VersionUrl = "http://app.ecjtu.net/api/v1/version";
    private String md5 = null;
    private Handler handler = new Handler() {

        public void handleMessage(Message message) {
                //本地化用户信息
                SharedPreUtil.getInstance().putUser((UserEntity) message.obj);
                //转跳到主界面
               // turn2mianActivity();
        }

    };
    private Handler getVersionHandler = new Handler(){
      public void handleMessage(Message message){
            if (message.obj != null){
                md5 = (String) message.obj;
                showNoticeDialog();
            }else{
                turn2mianActivity(null);
            }
      }
    };

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
        SharedPreUtil.initSharedPreference(getApplicationContext());
        setContentView(R.layout.activity_new_login);

        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);

        background = (RelativeLayout) findViewById(R.id.background);

        if (Build.VERSION.SDK_INT < 17) {        //兼容低版本
            background.setBackgroundDrawable(readBitMap(this, R.drawable.backgroud));
        }else background.setBackground(readBitMap(this, R.drawable.backgroud));
        site = (ImageView) findViewById(R.id.site);
        bottomLogo = (ImageView) findViewById(R.id.bottom_logo);
        new checkVersion(VersionUrl,this).start();
        propertyValuesHolder(site);
/*        UserEntity user = SharedPreUtil.getInstance().getUser();
        if (!TextUtils.isEmpty(user.getStudentID()) && !TextUtils.isEmpty(user.getToken())) {
            String loginName = user.getStudentID();
            String token = user.getToken();
            new myThead(loginName, null, loginUrl , token).start();
        }*/
    }

    public void propertyValuesHolder(View view)
    {

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha",
                0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX",
                0, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY",
                0, 1f);
        PropertyValuesHolder pvhH = PropertyValuesHolder.ofFloat("y",
                site.getY() + 500f , site.getY() + 250f );
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ, pvhH).setDuration(2000).start();
    }

    public static Drawable readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(),BitmapFactory.decodeStream(is,null,opt));
        return drawable;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * 显示软件更新对话框
     */
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
                turn2mianActivity(null);
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

    private class checkVersion extends Thread{


        private String url ;
        private Context context;
        public checkVersion(String url,Context context){
            this.url = url;
            this.context = context;
        }

        @Override
        public void run() {
            try {
                Log.i("tag","版本检查开始");
                HttpHelper getVersion = new HttpHelper();
                HashMap<String,Object> version = getVersion.getVersion(url);
                int versionCode = (int) version.get("versionCode");
                Log.i("tag","得到的版本号是："+versionCode);
                String versionName = (String) version.get("versionName");
                String md5 = (String) version.get("md5");
                int selfCode = getVersionCode();
                Message message = getVersionHandler.obtainMessage();
                Thread.sleep(3000);
                if (versionCode > selfCode){
                    Log.i("tag","我们需要更新");
                    message.obj = md5;
                }else{
                    Log.i("tag","我们不需要更新");
                    message.obj = null;
                }
                getVersionHandler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.run();
        }
        /*
         * 获取当前程序的版本号
         */
        private int getVersionCode() throws Exception{
            //获取packagemanager的实例
            PackageManager packageManager = getPackageManager();
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionCode;
        }

    }

    private class myThead extends Thread {//如果有用户信息，请求用户信息

        private String username;
        private String password;
        private String url;
        private String token;

        public myThead(String username, String password, String url,String token) {
            this.username = username;
            this.password = password;
            this.url = url;
            this.token = token;
        }



        @Override
        public void run() {
            Log.i("tag","个人信息请求开始");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.run();
            HttpHelper httpHelper = new HttpHelper();
            Message head = handler.obtainMessage();
            if(token == null) {
                token = httpHelper.passwordcheck(username, password, loginUrl);
            }
            if(token != null){
                UserEntity userEntity;
                userEntity = httpHelper.getUserContent(username,token,loginUrl);
                head.obj = userEntity;
            } else {
                head.obj = false;
            }
            handler.sendMessage(head);

        }
    }

}
