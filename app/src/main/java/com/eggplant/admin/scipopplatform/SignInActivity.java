package com.eggplant.admin.scipopplatform;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.edit;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.Connect;
import static com.eggplant.admin.scipopplatform.HttpHelper.POST;
import static com.eggplant.admin.scipopplatform.HttpHelper.isNetworkAvailable;
import static com.eggplant.admin.scipopplatform.HttpHelper.COOKIE;
import static com.eggplant.admin.scipopplatform.R.id.userClass;

public class SignInActivity extends AppCompatActivity {
    private Handler handler;
    /*
    * 用sharedPreference存储用户信息
    * */

    public SharedPreferences sharedPreferences;
    /*
    登陆post数据与服务器连接
    之后根据返回内容决定是否登陆成功
     */
    void connect(final String name, final String pass, final int userClass) {
         /*
        * 网络连接的线程
         */
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String body = "username=" + name + "&password=" + pass + "&phone=" + "" + "&userclass=" + userClass;
                JSONObject NormalUserresponse = null;
                String res = null;
                int info = UNKNOWN_WRONG;
                try {
                    res = Connect(getResources().getString(R.string.server) + "/login", body, POST);
                    Object json = new JSONTokener(res).nextValue();
                    //判断返回的是JsonArray还是JsonObject，以此来判断是否是管理员登陆
                    if (json instanceof JSONObject) {
                        NormalUserresponse = new JSONObject(res);
                        try {
                            info = (int)NormalUserresponse.get("info");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    else if(json instanceof JSONArray) {
                        /*
                        管理员返回的是管理员临时用户申请列表的字符串数据，直接传到AdminActivity处理就好了
                         */
                        info = ADMIN;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                /*
                 发送message给handler
                */
                Message message = Message.obtain(handler);
                if (res == null) {
                    message.what = WRONG_CODE;
                    message.sendToTarget();
                } else {
                    //处理返回数据
                    /*
                    info为返回消息标识
                    在configure里设定好
                     */
                    switch (info) {
                        case RIGHT:
                            message.what = RIGHT;
                            Bundle userInfo = new Bundle();
                            userInfo.putString("name",name);
                            userInfo.putString("pass",pass);
                            userInfo.putInt("class",userClass);
                            message.setData(userInfo);
                            message.sendToTarget();
                            break;
                        case ADMIN:
                            message.what = ADMIN;
                            Bundle tempUserList = new Bundle();
                            //传送临时用户申请列表的字符串数据
                            tempUserList.putString("tempUserList", res);
                            message.setData(tempUserList);
                            message.sendToTarget();
                            break;
                        default:
                            message.what = info;
                            message.sendToTarget();
                            break;
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        final EditText username = (EditText)findViewById(R.id.username);
        final EditText password = (EditText)findViewById(R.id.password);
        final RadioGroup radioGroup = (RadioGroup)findViewById(userClass);

        Button signin = (Button)findViewById(R.id.signin);
        Button signup = (Button)findViewById(R.id.signup);
        /*
        登陆按钮
         */
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString();
                String pass = password.getText().toString();
                JSONObject check = formatCheck(name, pass);
                try {
                    //格式检查成功进行网络连接
                    if (check.get("info") != null && check.get("info").equals(RIGHT)) {
                        /*
                        获取用户登陆类别
                        0 普通会员
                        1 专家会员
                        2 基地会员
                         */
                        final int userClass;
                        if (radioGroup.getCheckedRadioButtonId() == R.id.normal) {
                            userClass = NORMAL;
                        } else if (radioGroup.getCheckedRadioButtonId() == R.id.profession) {
                            userClass = PROFESSION;
                        } else {
                            userClass = BASE;
                        }
                        if(isNetworkAvailable(SignInActivity.this)) {
                            connect(name, pass, userClass);
                        } else {
                            Toast.makeText(getApplicationContext(), "无网络连接", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (check.get("info").equals(WRONG_NAMEFORMAT)) {
                            username.setText("");
                            Toast.makeText(getApplicationContext(), "用户名格式不正确", Toast.LENGTH_SHORT).show();
                        } else if (check.get("info").equals(WRONG_PASSFORMAT)) {
                            password.setText("");
                            Toast.makeText(getApplicationContext(), "密码格式不正确", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "出了一些问题", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_NAMEFORMAT:
                        username.setText("");
                        Toast.makeText(getApplicationContext(), "用户名格式不正确", Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_PASSFORMAT:
                        password.setText("");
                        Toast.makeText(getApplicationContext(), "密码格式不正确", Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_CLASS:
                        Toast.makeText(getApplicationContext(), "用户类别有误", Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_NAME:
                        username.setText("");
                        Toast.makeText(getApplicationContext(), "用户名不存在", Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_PASS:
                        password.setText("");
                        Toast.makeText(getApplicationContext(), "密码不正确", Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case DIFFERENT_CLASS:
                        Toast.makeText(getApplicationContext(), "用户类别错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        Bundle userInfo = message.getData();
                        /*
                        将用户姓名存储到sharedPreference中
                         */
                        sharedPreferences = getSharedPreferences(USER_PREFERENCE_NAME,MODE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", userInfo.getString("name"));
                        editor.putString("pass", userInfo.getString("pass"));
                        editor.putInt("class", userInfo.getInt("class"));
                        editor.commit();
                        /*
                        转到主页面
                         */
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        SignInActivity.this.startActivity(intent);
                        break;
                    case ADMIN:
                        Bundle tempUserList = message.getData();
                        Intent intent1 = new Intent(SignInActivity.this, AdminActivity.class);
                        intent1.putExtras(tempUserList);
                        SignInActivity.this.startActivity(intent1);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        /*
        注册按钮
         */
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                SignInActivity.this.startActivity(intent);
            }
        });

    }
    /*
    * 检查登陆信息的格式是否正确
    * 1 表示格式正确
    * 2 名字错误
    * 3 密码错误
     */
    JSONObject formatCheck(String name, String pass) {
        JSONObject result = new JSONObject();
        String usernamePattern = this.getString(R.string.usernamePattern);
        Pattern pattern = Pattern.compile(usernamePattern);
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            try {
                result.put("info", WRONG_NAMEFORMAT);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        String passwordPattern = this.getString(R.string.passwordPattern);
        pattern = Pattern.compile(passwordPattern);
        matcher = pattern.matcher(pass);
        if(!matcher.matches()) {
            try {
                result.put("info", WRONG_PASSFORMAT);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            result.put("info", RIGHT);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return result;
        }
    }


}
