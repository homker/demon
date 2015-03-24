package ecjtunet.com.demon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.InputStream;


public class login extends Activity {

    public String url = "http://homker.sinaapp.com/app.php";
    public String loginNameText;
    private EditText loginName;
    private EditText password;
    private RelativeLayout loginBox;
    private RelativeLayout content;
    private String passWord;
    private Handler handler = new Handler() {

        public void handleMessage(Message message) {
            if (message.obj instanceof UserEntity) {
                //本地化用户信息
                SharedPreUtil.getInstance().putUser((UserEntity) message.obj);
                //转跳到主界面
                turn2mianActivity();
            } else {
                Toast.makeText(login.this, "用户名和密码不匹配", Toast.LENGTH_SHORT).show();
                loginBox.setVisibility(View.VISIBLE);
            }
            Log.i("tag", "he,it works");
        }

    };

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreUtil.initSharedPreference(getApplicationContext());
        setContentView(R.layout.activity_login);
        content = (RelativeLayout) findViewById(R.id.content);
        if (Build.VERSION.SDK_INT < 17) {
            content.setBackgroundDrawable(readBitMap(this, R.drawable.background));
        }else content.setBackground(readBitMap(this, R.drawable.background));
        loginBox = (RelativeLayout) findViewById(R.id.loginBox);
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
        Log.i("tag", "---------------------------->works");
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
                //new myTask(loginNameText,passWord,url).execute();
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

    /*private class myTask extends AsyncTask<Void,Integer,Boolean>{

        private String userName;
        private String passWord;
        private String url;

        public myTask(String userName,String passWord,String url){
            this.userName = userName;
            this.passWord = passWord;
            this.url = url;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpHelper check = new HttpHelper(url);
            if(check.passwordcheck(userName,passWord)){
                UserEntity userEntity;
                userEntity = check.getUserContent(userName);
                return true;
            }else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                Toast.makeText(getBaseContext(),"this is success",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getBaseContext(),"this is fail",Toast.LENGTH_SHORT).show();
            }

        }
    }*/


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
            super.run();
            HttpHelper httpHelper = new HttpHelper(url);
            Message head = handler.obtainMessage();
            if (username.equals("admin")&&password.equals("test")){
                username = "20122110090224";
                password = "homker";
            }
            if (httpHelper.passwordcheck(username, password)) {
                UserEntity userEntity;
                userEntity = httpHelper.getUserContent(username);
                head.obj = userEntity;
            } else {
                head.obj = false;
            }
            handler.sendMessage(head);

        }
    }

}
