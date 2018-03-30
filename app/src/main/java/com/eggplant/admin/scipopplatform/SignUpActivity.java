package com.eggplant.admin.scipopplatform;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.RunnableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static android.R.id.edit;
import static android.R.id.message;
import static com.eggplant.admin.scipopplatform.Configure.BASE;
import static com.eggplant.admin.scipopplatform.Configure.DIFFERENT_PASS;
import static com.eggplant.admin.scipopplatform.Configure.NEED_WAIT;
import static com.eggplant.admin.scipopplatform.Configure.NORMAL;
import static com.eggplant.admin.scipopplatform.Configure.PROFESSION;
import static com.eggplant.admin.scipopplatform.Configure.REPEATEDNAME;
import static com.eggplant.admin.scipopplatform.Configure.RIGHT;
import static com.eggplant.admin.scipopplatform.Configure.UNKNOWN_WRONG;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_CLASS;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_CODE;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_NAME;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_NAMEFORMAT;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_PASS;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_PASSFORMAT;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_PHONE;
import static com.eggplant.admin.scipopplatform.HttpHelper.*;
import static com.eggplant.admin.scipopplatform.R.id.userClass;
import static com.eggplant.admin.scipopplatform.SignInActivity.MODE;
import static com.eggplant.admin.scipopplatform.SignInActivity.PREFERENCE_NAME;

/**
 * Created by admin on 2018/3/11.
 */

public class SignUpActivity extends AppCompatActivity {
    private Handler handler;
    /*
    * 用sharedPreference存储用户姓名
    * */
    public static final String PREFERENCE_NAME = "user";
    public static final int MODE = MODE_PRIVATE;
    public SharedPreferences sharedPreferences;
    /*
    注册网络连接
     */
    void connect(final String name, final String pass, final String phone, final int userClass) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String body = "username=" + name + "&password=" + pass + "&phone=" + phone + "&userclass=" + userClass;
                JSONObject response = null;
                try {
                    response = new JSONObject(Connect(getResources().getString(R.string.server) + "/regist", body, POST));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                /*
                 发送message给handler
                */
                Message message = Message.obtain(handler);
                if (response == null) {
                    message.what = WRONG_CODE;
                    message.sendToTarget();
                } else {
                    //处理返回数据
                    int info = UNKNOWN_WRONG;
                    try {
                        info = (int)response.get("info");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    switch (info) {
                        case 2:
                            message.what = WRONG_NAMEFORMAT;
                            message.sendToTarget();
                            break;
                        case 3:
                            message.what = WRONG_PASSFORMAT;
                            message.sendToTarget();
                            break;
                        case 5:
                            message.what = WRONG_CLASS;
                            message.sendToTarget();
                            break;
                        case 4:
                            message.what = WRONG_PHONE;
                            message.sendToTarget();
                            break;
                        case 0:
                            message.what = REPEATEDNAME;
                            message.sendToTarget();
                            break;
                        case 1:
                            message.what = RIGHT;
                            Bundle userInfo = new Bundle();
                            userInfo.putString("name",name);
                            userInfo.putString("pass",pass);
                            userInfo.putInt("class",userClass);
                            message.setData(userInfo);
                            message.sendToTarget();
                            break;
                        case 8:
                            message.what = NEED_WAIT;
                            message.sendToTarget();
                            break;
                        default:
                            message.what = UNKNOWN_WRONG;
                            message.sendToTarget();
                            break;
                    }
                }
            }
        };
        new Thread(runnable).start();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        Button signin = (Button)findViewById(R.id.signin);
        /*
        转到登陆
         */
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                SignUpActivity.this.startActivity(intent);
            }
        });

        final EditText username = (EditText)findViewById(R.id.username);
        final EditText password = (EditText)findViewById(R.id.password);
        final EditText conPass = (EditText)findViewById(R.id.confirmPass);
        final EditText phone = (EditText)findViewById(R.id.phone);
        final RadioGroup radioGroup = (RadioGroup)findViewById(userClass);

        Button signup = (Button)findViewById(R.id.signup);
        /*
        注册用户
         */
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString();
                String pass = password.getText().toString();
                String con_pass = conPass.getText().toString();
                String phone_num = phone.getText().toString();
                JSONObject check = formatCheck(name, pass, con_pass, phone_num);
                //格式检查成功进行网络连接
                try {
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
                        if (isNetworkAvailable(SignUpActivity.this)) {
                            connect(name, pass, phone_num, userClass);
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
                        } else if (check.get("info").equals(DIFFERENT_PASS)){
                            Toast.makeText(getApplicationContext(), "确认密码不一致", Toast.LENGTH_SHORT).show();
                        } else if (check.get("info").equals(WRONG_PHONE)) {
                            Toast.makeText(getApplicationContext(), "电话号码格式不正确", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "用户类别选择不正确", Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_PHONE:
                        phone.setText("");
                        Toast.makeText(getApplicationContext(), "电话号码格式不正确", Toast.LENGTH_SHORT).show();
                        break;
                    case REPEATEDNAME:
                        Toast.makeText(getApplicationContext(), "已有相同的用户名", Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        message.what = RIGHT;
                        Bundle userInfo = message.getData();
                       /*
                        将用户姓名密码存储到sharedPreference中
                         */
                        sharedPreferences = getSharedPreferences(PREFERENCE_NAME,MODE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", userInfo.getString("name"));
                        editor.putString("pass", userInfo.getString("pass"));
                        editor.putInt("class", userInfo.getInt("class"));
                        editor.commit();
                        /*
                        转到主页面
                         */
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        SignUpActivity.this.startActivity(intent);
                        break;
                    case NEED_WAIT:
                        Toast.makeText(getApplicationContext(), "专家会员和基地会员需要审核通过", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

    }
    /*
    注册时的表单格式检查
     */
    private JSONObject formatCheck(String name, String pass, String con_pass, String phone) {
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

        if (!con_pass.equals(pass)) {
            try {
                result.put("info", DIFFERENT_PASS);
                return result;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        String phonePattern = this.getString(R.string.phonePattern);
        pattern = Pattern.compile(phonePattern);
        matcher = pattern.matcher(phone);
        if (!matcher.matches()) {
            try {
                result.put("info", WRONG_PHONE);
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
