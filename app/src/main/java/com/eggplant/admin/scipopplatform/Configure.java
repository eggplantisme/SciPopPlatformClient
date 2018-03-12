package com.eggplant.admin.scipopplatform;

/**
 * Created by admin on 2018/3/10.
 */

public class Configure {
    /*
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


}
