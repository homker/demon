package ecjtu.net.demon;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/*
* download Service
* */
public class DownloadService extends Service {

    private static final int duration = 300;
    private static final String apkUrl = "http://app.ecjtu.net/download";
    private static final String rxApk = "rixin.apk";
    private final static String VersionUrl = "http://app.ecjtu.net/api/v1/version";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private String md5;
    private String mSavePath = null;
    private MyBinder myBinder = new MyBinder();

    public DownloadService() {
    }

    public IBinder onBinder(Intent intent) {
        return myBinder;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.i("tag", "the download service is work");
        super.onCreate();
        initNotification();
    }

    private void initNotification() {
        Log.i("tag", "更新开始");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("更新中")//设置通知栏标题
                .setContentText("正在下载。。。")//设置通知栏显示内容
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
        //版本更新
        checkVersion();
    }

    private void DownLoadApk() {
        HttpAsync.get(apkUrl, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onStart() {
                Log.i("tag", "下载开始");
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                mBuilder.setContentText("更新失败");
                mNotificationManager.notify(1, mBuilder.build());
                ToastMsg.builder.display("更新失败", duration);
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
                File apkfile = new File(mSavePath, rxApk);
                file.renameTo(apkfile);
                //安装文件
                mBuilder.setContentText("更新成功~！");
                mBuilder.setProgress(0, 0, false);
                mNotificationManager.notify(1, mBuilder.build());
                installApk(apkfile);
            }
        });
    }

    private void DownloadByAndroid(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void installApk(File apkfile) {
        if (!apkfile.exists() && md5 == HttpHelper.getFileMD5(apkfile)) {
            return;
        }
        //通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        this.startActivity(i);
    }

    private void checkVersion() {
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
                ToastMsg.builder.display("网络请求失败", 300);
            }
        });
    }

    private void showDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        //更新
        builder.setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框�
                DownLoadApk();
            }
        });
        // 稍后更新
        builder.setNegativeButton(R.string.soft_update_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    private int getVersionCode() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionCode;
    }

    public class MyBinder extends Binder {

        public DownloadService startDownLoad() {
            return DownloadService.this;
        }

    }


}
