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
public class MyService extends Service {

    private static final int duration = 300;
    private static final String apkUrl = "http://app.ecjtu.net/download";
    private static final String rxApk = "rixin.apk";
    private final static String VersionUrl = "http://app.ecjtu.net/api/v1/version";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private String md5;
    private String mSavePath = null;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
        DownLoadApk();
    }

    private void initNotification() {
        Log.i("tag", "���¿�ʼ");

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("������")//����֪ͨ������
                .setContentText("�������ء�����") //����֪ͨ����ʾ����
                .setTicker("��ʼ����") //֪ͨ�״γ�����֪ͨ��������������Ч����
                .setWhen(System.currentTimeMillis())//֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��
                .setPriority(Notification.PRIORITY_DEFAULT) //���ø�֪ͨ���ȼ�
                .setOngoing(false)//ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
                .setDefaults(Notification.DEFAULT_LIGHTS)//��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������
                .setSmallIcon(R.drawable.logo_cycle)
                .setProgress(100, 0, false);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(1, notification);
        // �����ļ�
        checkVersion();
    }

    private void DownLoadApk() {
        HttpAsync.get(apkUrl, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onStart() {
                Log.i("tag", "���ؿ�ʼ");
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                mBuilder.setContentText("����ʧ��~��");
                mNotificationManager.notify(1, mBuilder.build());
                ToastMsg.builder.display("����ʧ��", duration);
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
                // ��װ�ļ�
                mBuilder.setContentText("���³ɹ�~��");
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
        // ͨ��Intent��װAPK�ļ�
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
                        Log.i("tag", "��Ҫ����");
                        showDialog();
                    } else {
                        Log.i("tag", "���ǲ���Ҫ����");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastMsg.builder.display("��������ʧ��", 300);
                //Toast.makeText(New_login.this, "��������ʧ��", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialog() {
        // ����Ի���
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        // ����
        builder.setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // ��ʾ���ضԻ���
                DownLoadApk();
            }
        });
        // �Ժ����
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
        //��ȡpackagemanager��ʵ��
        PackageManager packageManager = getPackageManager();
        //getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionCode;
    }

}
