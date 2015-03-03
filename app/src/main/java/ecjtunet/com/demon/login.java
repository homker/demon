package ecjtunet.com.demon;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class login extends Activity {

    public String url = "http://homker.sinaapp.com/app.php";
    public String loginNameText;
    private EditText loginName;
    private EditText password;
    private Handler handler = new Handler() {

        public void handleMessage(Message message) {
            if (message.obj instanceof UserEntity) {
                //本地化用户信息
                SharedPreUtil.getInstance().putUser((UserEntity) message.obj);
                //转跳到主界面
                turn2mianActivity();
            } else {
                Toast.makeText(login.this, "用户名和密码不匹配", Toast.LENGTH_SHORT).show();
            }
            Log.i("tag", "he,it works");
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreUtil.initSharedPreference(getApplicationContext());
        setContentView(R.layout.login);
        RelativeLayout loginBox = (RelativeLayout) findViewById(R.id.loginBox);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Log.i("tag", "----------------->" + String.valueOf(imageView));
        Log.i("tag", "---------------------------------------->" + String.valueOf(loginBox));
        UserEntity user = SharedPreUtil.getInstance().getUser();
        Log.i("tag", "user:" + user.getStudentID() + "_____password" + user.getPassword());
        if (!TextUtils.isEmpty(user.getStudentID()) && !TextUtils.isEmpty(user.getPassword())) {
            loginBox.setVisibility(View.GONE);
            String loginName = user.getStudentID();
            String passWord = user.getPassword();
            try {
                Toast.makeText(login.this, "验证中...", Toast.LENGTH_SHORT).show();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new myThead(loginName, passWord, url).start();
        }

        loginName = (EditText) findViewById(R.id.loginName);
        password = (EditText) findViewById(R.id.passWord);


    }

    public void loginClick(View view) {
        logincheck();
    }

    private void turn2mianActivity() {
        Intent intent = new Intent();
        intent.setClass(login.this, main.class);
        startActivity(intent);
        finish();
    }

    /**
     * 登入检查，是否为空，是否正确
     *
     * @return boolean
     */

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private boolean logincheck() {
        loginNameText = loginName.getText().toString();
        String passWordText = password.getText().toString();
        if (loginNameText.isEmpty()) {
            Toast.makeText(login.this, "学号不得为空", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (passWordText.isEmpty()) {
                Toast.makeText(login.this, "密码不得为空", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(login.this, "验证中...", Toast.LENGTH_SHORT).show();
                new myThead(loginNameText, passWordText, url).start();
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class myThead extends Thread {

        private String username;
        private String password;
        private String url;

        public myThead(String username, String password, String url) {
            this.username = username;
            this.password = password;
            this.url = url;
        }

        @Override
        public void run() {
            HttpHelper httpHelper = new HttpHelper(url);
            Message head = handler.obtainMessage();
            if (httpHelper.passwordcheck(username, password)) {
                UserEntity userEntity;
                userEntity = httpHelper.getUserContent(username);
                head.obj = userEntity;
            } else {
                head.obj = false;
            }
            handler.sendMessage(head);
            super.run();
        }
    }

}
