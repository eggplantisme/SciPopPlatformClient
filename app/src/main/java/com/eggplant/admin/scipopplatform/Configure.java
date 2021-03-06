package com.eggplant.admin.scipopplatform;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by admin on 2018/3/10.
 */

public class Configure {
    /*
    客户端服务端的约定码
    -1 表示返回码错误
    0 注册时名字重复
    1 返回正确
    2 名字格式错误
    3 密码格式错误
    4 电话格式错误
    5 用户类别格式错误
    6 登陆时名字不存在
    7 登陆时密码错误
    -10 未知错误
     */
    public static final int WRONG_CODE = -1;
    public static final int REPEATEDNAME = 0;
    public static final int RIGHT = 1;
    public static final int WRONG_NAMEFORMAT = 2;
    public static final int WRONG_PASSFORMAT = 3;
    public static final int WRONG_PHONE = 4;
    public static final int WRONG_CLASS = 5;
    public static final int WRONG_NAME = 6;
    public static final int WRONG_PASS = 7;
    public static final int UNKNOWN_WRONG = -10;
    public static final int DIFFERENT_PASS = 9;
    public static final int NEED_WAIT = 8;
    public static final int ADMIN = 11;
    public static final int NOT_ADMIN = 12;
    public static final int DIFFERENT_CLASS = 13;
    public static final int NO_RIGHT = 14;


    /*
    科普信息和基地的区分
     */
    public static final int SCIINFO = 0;
    public static final int SCIBASE = 1;


    /*
    用户类别
     */
    public static final int NORMAL = 0;
    public static final int PROFESSION = 1;
    public static final int BASE = 2;

    public static final String SERVER = "http://192.168.199.127:8080/SciencePop";
    public static final String USER_PREFERENCE_NAME = "user";
    public static final int MODE = MODE_PRIVATE;

}
