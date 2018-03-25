package com.eggplant.admin.scipopplatform;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.autoUrlDetect;
import static android.R.attr.name;
import static android.R.id.message;
import static com.eggplant.admin.scipopplatform.Configure.UNKNOWN_WRONG;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_CLASS;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_NAME;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_NAMEFORMAT;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_PASS;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_PASSFORMAT;
import static com.eggplant.admin.scipopplatform.R.id.userClass;

/**
 * Created by admin on 2018/3/11.
 */

public class HttpHelper {
    /*
    网络检测
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connect = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect != null) {
            NetworkInfo networkInfo = connect.getActiveNetworkInfo();
            if (networkInfo == null) {
                return false;
            } else if (networkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }
    /*
    cookie
     */
    public static String COOKIE = "";

    public static int GET = 0;
    public static int POST = 1;

    /*
    Http连接
    返回Json数据
    根据参数选择GET， POST方式
    url:请求的地址
    data post的数据，GET时为null
    way是GET，POST的选择,除此之外返回null
    网络连接出错也返回null
     */
    public static String Connect(String url, String data, int way) {
        HttpURLConnection connection = null;
        try {
            URL _url = new URL(url);
            connection = (HttpURLConnection) (_url.openConnection());
            /*
            负责自动保存cookie，以及返回cookie
             */
            CookieManager manager = new CookieManager();
            CookieHandler.setDefault(manager);
            if (way == GET) {
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10 * 1000);
                connection.connect();
            } else if (way == POST) {
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(10 * 1000);
                connection.setRequestProperty("Content-Length", String.valueOf(data.length()));
                //设置请求内容
                connection.setDoOutput(true);
                connection.getOutputStream().write(data.getBytes());
                //连接
                connection.connect();
            } else {
                return null;
            }
            int code = connection.getResponseCode();
            if (code == connection.HTTP_OK) {
                //解析获得数据
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                //测试展示
                Log.v("res", response.toString());
                //获得Cookie
                COOKIE = connection.getHeaderField("Set-Cookie");
                Log.v("cookie", COOKIE);
                return response.toString();
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
