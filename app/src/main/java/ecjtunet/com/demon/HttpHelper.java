package ecjtunet.com.demon;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by homker on 2015/1/24.
 */
public class HttpHelper {
    private String url;
    private String result = null;
    private URL url1 = null;
    private HttpURLConnection connection = null;
    private InputStreamReader in = null;
    public static HttpClient customerHttpClient;

    public HttpHelper(String url) {
        this.url = url;
    }

    public static synchronized HttpClient getHttpClient() {
        if (null== customerHttpClient) {
            HttpParams params =new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,"utf-8");
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams
                    .setUserAgent(
                            params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    +"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            // 超时设置
/* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, 1000);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 2000);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 4000);

            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg =new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(
                    params, schReg);
            customerHttpClient =new DefaultHttpClient(conMgr, params);
        }
        return customerHttpClient;
    }

    /**
     * 系统级别的get调用 不建议使用
     *
     * @return String result
     */
    public String get() {
        try {
            url1 = new URL(url);
            connection = (HttpURLConnection) url1.openConnection();
            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            result = stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * 系统级别的post调用 不建议使用
     *
     * @param datas
     * @return
     */
    public String post(ArrayList<HashMap<String, String>> datas) {
        try {
            url1 = new URL(url);
            connection = (HttpURLConnection) url1.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            for (HashMap<String, String> hashMap : datas) {
                String key = hashMap.get("key");
                String value = hashMap.get("value");
                dataOutputStream.writeBytes(key + "=" + value);
            }
            dataOutputStream.writeBytes("token=rx");
            dataOutputStream.flush();
            dataOutputStream.close();

            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            result = stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * Apache 封装的get调用，建议使用
     *
     * @return String result
     */
    public String apacheGet() {
        String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = getHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));

            StringBuffer strBuffer = new StringBuffer("");
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * Apache 封装的post调用，建议调用
     *
     * @param datas
     * @return
     */
    public String apachePost(ArrayList<HashMap<String, String>> datas) {
        String result = null;
        BufferedReader reader = null;
        try {
            HttpClient client = getHttpClient();
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            for (HashMap<String, String> hashMap : datas) {
                String key = hashMap.get("key");
                String value = hashMap.get("value");
                postParameters.add(new BasicNameValuePair(key, value));
            }
            postParameters.add(new BasicNameValuePair("token", "homker"));

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                    postParameters);
            request.setEntity(formEntity);
            Log.i("tag", String.valueOf(request));
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));

            StringBuffer strBuffer = new StringBuffer("");
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * 账户密码检查
     *
     * @param userName
     * @param passWord
     * @return
     */
    public boolean passwordcheck(String userName, String passWord) {
        int statues = 0;
        boolean flag = false;
        ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("key", "userName");
        hashMap.put("value", userName);
        HashMap<String, String> hashMap1 = new HashMap<String, String>();
        hashMap1.put("key", "passWord");
        hashMap1.put("value", passWord);
        datas.add(hashMap);
        datas.add(hashMap1);
        String result = apachePost(datas);
        Log.i("tag", "result:" + result);
        try {
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

            statues = jsonObject.getInt("statues");

            flag = jsonObject.getBoolean("result");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (statues == 200) {
            return flag;
        } else {
            return false;
        }
    }

    /**
     * 获取用户信息
     *
     * @param studentID
     * @return
     */
    public UserEntity getUserContent(String studentID) {
        Log.i("tag", "it works");
        JSONObject person = null;
        int status = 0;
        UserEntity userEntity = new UserEntity();
        this.url = url + "?person=" + studentID;
        String result = apacheGet();
        JSONTokener jsonTokener = new JSONTokener(result);
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            status = jsonObject.getInt("status");
            person = jsonObject.getJSONObject("person");
            userEntity.setStudentID(person.getString("studentID"));
            userEntity.setPassword(person.getString("passWord"));
            userEntity.setUserName(person.getString("userName"));
            userEntity.setHeadImage(person.getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("tag", "url:" + url);
        Log.i("tag", "result:" + String.valueOf(person));
        return userEntity;
    }

    /**
     * 获取新闻列表
     *
     * @return
     */
    public ArrayList<HashMap<String, Object>> getNewsList() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        int status;
        this.url = url + "?newslist=rx";
        String result = apacheGet();
        JSONTokener jsonTokener = new JSONTokener(result);
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            status = jsonObject.getInt("status");
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, Object> item = new HashMap<String, Object>();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                item.put("imageDrawable", getImage(jsonObject1.getString("image")));
                item.put("title", jsonObject1.getString("title"));
                item.put("info", jsonObject1.getString("info"));
                item.put("flag", jsonObject1.getString("flag"));
                item.put("articleID", jsonObject1.getString("articleID"));
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据url请求图片并返回drawable
     *
     * @param imageUrl
     * @return
     */
    public Drawable getImage(String imageUrl) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    public HashMap<String, Object> getNewsContent(String articleID) {
        HashMap<String, Object> reback = new HashMap<String, Object>();
        ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();
        int status;
        String title;
        String author;
        String dataTime;
        String tag;
        this.url = url + "?articleID=" + articleID;
        String result = apacheGet();
        Log.i("tag", "===================================" + this.url);
        Log.i("tag", "result:" + result);
        JSONTokener jsonTokener = new JSONTokener(result);
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            status = jsonObject.getInt("status");
            title = jsonObject.getString("title");
            author = jsonObject.getString("author");
            dataTime = jsonObject.getString("dataTime");
            tag = jsonObject.getString("tag");
            JSONArray article = jsonObject.getJSONArray("article");
            for (int i = 0; i < article.length(); i++) {
                JSONObject jsonObject1 = article.getJSONObject(i);
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("type", jsonObject1.getString("type"));
                hashMap.put("value", jsonObject1.getString("value"));
                datas.add(hashMap);
            }
            reback.put("status", status);
            reback.put("title", title);
            reback.put("author", author);
            reback.put("dataTime", dataTime);
            reback.put("tags", tag);
            reback.put("article", datas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reback;
    }

}
