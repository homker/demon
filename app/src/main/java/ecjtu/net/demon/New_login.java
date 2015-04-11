package ecjtu.net.demon;

import android.animation.Animator;
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
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;


public class New_login extends ActionBarActivity {
    private ImageView site;
    private final static String VersionUrl = "http://app.ecjtu.net/api/v1/version";
    private String md5 = null;
    private boolean update = false;
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

        RelativeLayout background = (RelativeLayout) findViewById(R.id.background);

        if (Build.VERSION.SDK_INT < 17) {        //兼容低版本
            background.setBackgroundDrawable(readBitMap(this, R.drawable.backgroud));
        }else background.setBackground(readBitMap(this, R.drawable.backgroud));
        site = (ImageView) findViewById(R.id.site);
        propertyValuesHolder(site);
        checkVersionAsync();
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
                        update = true;
                        showDialog();
                    } else {
                        Log.i("tag", "我们不需要更新");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastMsg.builder.display("网络请求失败",300);
                //Toast.makeText(New_login.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void propertyValuesHolder(View view) {
        Log.i("tag","动画已经被执行");
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 0, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 0, 1f);
        PropertyValuesHolder pvhH = PropertyValuesHolder.ofFloat("y", site.getY() + 500f, site.getY() + 350f);
        Animator objectAnimator =  ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ, pvhH).setDuration(2000);
        objectAnimator.start();
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!update) {
                    turn2mianActivity(null);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
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


    private void showDialog(){
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

    private int getVersionCode() throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionCode;
    }


    private void downloadApk(){
        Bundle bundle = new Bundle();
        bundle.putString("update", md5);
        turn2mianActivity(bundle);
    }
}
