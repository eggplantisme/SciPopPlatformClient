package com.eggplant.admin.scipopplatform;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;
import static android.R.id.message;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.Connect;
import static com.eggplant.admin.scipopplatform.HttpHelper.GET;
import static com.eggplant.admin.scipopplatform.HttpHelper.POST;
import static com.eggplant.admin.scipopplatform.R.id.base;
import static com.eggplant.admin.scipopplatform.R.id.basename;
import static com.eggplant.admin.scipopplatform.R.id.infoId;

/**
 * Created by admin on 2018/4/2.
 */

public class AdminUtils {
    public static List<String> baseNameList;
    public static List<String> baseIdList;
    /*
    加载base信息的id和name部分进入两个List
    异步版
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
                        handler.sendMessage(message);
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
    加载base信息的id和name部分进入两个List
    同步版
     */
    public static void loadBaseSync() {
        baseIdList = new ArrayList<>();
        baseNameList = new ArrayList<>();
        baseIdList.add("-1");
        baseNameList.add("地球");
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                        handler.sendMessage(message);
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            handler.sendMessage(message);
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            handler.sendMessage(message);
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
                        handler.sendMessage(message);
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            handler.sendMessage(message);
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            handler.sendMessage(message);
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
                        handler.sendMessage(message);
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            handler.sendMessage(message);
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            handler.sendMessage(message);
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
    获取专家会员的全部文章
    排序用
     */
    public static void loadProPageList(final String name, final Handler handler, final List<Map<String, Object>> data) {
                try {
                    String url = SERVER + "/getSciPopInfos/" + name;
                    String res = Connect(url, null, GET);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                    } else {
                        JSONArray ListData;
                        ListData = new JSONArray(res);
                        data.clear();
                        for (int i = 0; i < ListData.length(); i++) {
                            JSONObject jsonObject;
                            jsonObject = ListData.getJSONObject(i);
                            Map<String, Object> temp = new LinkedHashMap<>();
                            temp.put("infoId", jsonObject.get("infoId"));
                            temp.put("title", jsonObject.get("title"));
                            temp.put("content", jsonObject.get("content"));
                            temp.put("lastTime", jsonObject.get("lastTime").toString());
                            temp.put("baseName", jsonObject.get("baseId"));//暂时用baseId带替baseName
                            data.add(temp);
                        }
                        message.what = RIGHT;
                    }
                    if (handler != null) {
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

    /*
    科普基地的添加
     */
    public static void baseAdd(final String basename, final String baseInfo, final String baseAddress, final String baseContact, final String baseAdminName, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Date curdate = new Date(System.currentTimeMillis());
                String url = SERVER + "/addSciPopBase";
                String body = "baseName=" + basename + "&baseInfo=" + baseInfo + "&address=" + baseAddress + "&contactNumber=" + baseContact + "&baseAdminName=" + baseAdminName;
                try {
                    String res = Connect(url, body, POST);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                        handler.sendMessage(message);
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            handler.sendMessage(message);
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            handler.sendMessage(message);
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
    科普基地的删除
     */
    public static void baseDelete(final String baseId, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = SERVER + "/deleteSciPopBase/" + baseId;
                try {
                    String res = Connect(url, null, GET);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                        handler.sendMessage(message);
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            handler.sendMessage(message);
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            handler.sendMessage(message);
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
    科普基地的修改
     */
    public static void baseUpdate(final String baseId, final String basename, final String baseInfo, final String baseAddress, final String baseContact, final String baseAdminName, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Date curdate = new Date(System.currentTimeMillis());
                String url = SERVER + "/updateSciPopBase";
                String body = "baseId=" + baseId + "&baseName=" + basename + "&baseInfo=" + baseInfo + "&address=" + baseAddress + "&contactNumber=" + baseContact + "&baseAdminName=" + baseAdminName;
                try {
                    String res = Connect(url, body, POST);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                        handler.sendMessage(message);
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getString("update").equals("yes")) {
                            message.what = RIGHT;
                            handler.sendMessage(message);
                        } else if (jsonObject.getString("update").equals("no")) {
                            message.what = NO_RIGHT;
                            handler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

}
