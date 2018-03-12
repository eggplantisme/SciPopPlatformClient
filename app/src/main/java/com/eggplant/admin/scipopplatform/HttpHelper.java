package com.eggplant.admin.scipopplatform;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

}
