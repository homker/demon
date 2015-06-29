package ecjtu.net.demon.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import ecjtu.net.demon.R;
import ecjtu.net.demon.activitys.LoginActivity;
import ecjtu.net.demon.activitys.webview;
import ecjtu.net.demon.utils.HttpAsync;
import ecjtu.net.demon.utils.SharedPreUtil;
import ecjtu.net.demon.utils.ToastMsg;
import ecjtu.net.demon.utils.UserEntity;

/**
 * Created by 圣麟 on 2015/6/13.
 */
public class comment_text extends Fragment {

    private View view;
    private Button submitBtn;
    public static EditText commentText;
    private static InputMethodManager imm;
    private static FragmentTransaction transaction;
    private static String url;
    private Context context;
    private WebView webView;

    /**
     * 构造函数用以传递参数
     * @param context
     * @param url
     */
    public comment_text(Context context, String url,WebView webView){
        this.context = context;
        this.url = url;
        this.webView = webView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        webview.isComment = true;
        view = inflater.inflate(R.layout.comment_text, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        submitBtn = (Button) view.findViewById(R.id.submitBtn);
        commentText = (EditText) view.findViewById(R.id.commentText);
        transaction = getFragmentManager().beginTransaction();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("tag","---------submit!!!----------");
                if (!TextUtils.isEmpty(commentText.getText().toString())){
                    postComment(commentText.getText().toString());
                }
                imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
                transaction.replace(R.id.comment_layout, webview.commentBtn).commit();
            }
        });
        commentText.setFocusable(true);
        commentText.setFocusableInTouchMode(true);
        commentText.requestFocus();
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void postComment(String comment){
        UserEntity userEntity = SharedPreUtil.getInstance().getUser();
        String token = userEntity.getToken();
        if(!TextUtils.isEmpty(token)){
            String sid = userEntity.getStudentID();
            RequestParams params = new RequestParams();
            params.put("sid",sid);
            params.put("token",token);
            params.put("content",comment);
            HttpAsync.post(url,params, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    int status = 0;
                    try {
                        status = response.getInt("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status == 200){
                        webView.reload();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    ToastMsg.builder.display("请求失败",300);
                }
            });
        }else{
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            context.startActivity(intent);
        }
        //
    }

    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
            transaction.replace(R.id.comment_layout, webview.commentBtn).commit();
        }
        return true;
    }
}
