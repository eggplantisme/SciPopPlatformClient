package com.eggplant.admin.scipopplatform;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.Connect;
import static com.eggplant.admin.scipopplatform.HttpHelper.GET;
import static com.eggplant.admin.scipopplatform.HttpHelper.POST;
import static com.eggplant.admin.scipopplatform.R.id.infoId;

/**
 * Created by admin on 2018/4/2.
 */

public class AdminUtils {
    public static List<String> baseNameList;
    public static List<String> baseIdList;
    /*
    加载base信息的id和name部分进入两个List
     */
    public static void loadBase(final Handler handler) {
        baseIdList = new ArrayList<>();
        baseNameList = new ArrayList<>();
        baseIdList.add("-1");
        baseNameList.add("地球");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = SERVER + "/getBaseList";
                    String res = Connect(url, null, GET);
                    JSONArray baseList = new JSONArray(res);
                    for (int i = 0; i < baseList.length(); i++) {
                        try {
                            JSONObject jsonObject = baseList.getJSONObject(i);
                            if (!jsonObject.getString("id").equals("-1")) {
                                baseNameList.add(jsonObject.getString("title"));
                                baseIdList.add(String.valueOf(jsonObject.getInt("id")));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = null;
                    while (true) {
                        if (handler == null) {
                        } else {
                            message = Message.obtain(handler);
                            break;
                        }
                    }
                    if (res == null) {
                        message.what = WRONG_CODE;
                        message.sendToTarget();
                    } else {
                        message.what = RIGHT;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }
    /*
    科普信息的添加
     */
    public static void proAdd(final String title, final String content, final String baseId, final String name, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Date curdate = new Date(System.currentTimeMillis());
                String url = SERVER + "/addSciPopInfo";
                String body = "title=" + title + "&writterName=" + name + "&content=" + content + "&lastTime=" + curdate.toString() + "&baseId=" + baseId;
                try {
                    String res = Connect(url, body, POST);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                        message.sendToTarget();
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            message.sendToTarget();
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            message.sendToTarget();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }
    /*
    科普信息的删除
     */
    public static void proDelete(final String infoId, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = SERVER + "/deleteSciPopInfo/" + infoId;
                try {
                    String res = Connect(url, null, GET);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                        message.sendToTarget();
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            message.sendToTarget();
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            message.sendToTarget();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }
    /*
    科普信息的修改
     */
    public static void proUpdate(final String infoId, final String title, final String content, final String baseId, final String name, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Date curdate = new Date(System.currentTimeMillis());
                String url = SERVER + "/updateSciPopInfo";
                String body = "infoId=" + infoId + "&title=" + title + "&writterName=" + name + "&content=" + content + "&lastTime=" + curdate.toString() + "&baseId=" + baseId;
                try {
                    String res = Connect(url, body, POST);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                        message.sendToTarget();
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            message.sendToTarget();
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            message.sendToTarget();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    /*
    科普基地的添加
     */
    public static void baseAdd(final String basename, final String baseInfo, final String baseAddress, final String baseContact, String baseAdminName, Handler handler) {}
    /*
    科普基地的删除
     */
    public static void baseDelete(final String baseId, Handler handler) {}
    /*
    科普基地的修改
     */
    public static void baseUpdate(final String basename, final String baseInfo, final String baseAddress, final String baseContact, String baseAdminName, Handler handler) {}

}
