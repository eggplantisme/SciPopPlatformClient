package com.eggplant.admin.scipopplatform;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.eggplant.admin.scipopplatform.R.id.base;
import static com.eggplant.admin.scipopplatform.R.id.baseChoose;
import static com.eggplant.admin.scipopplatform.R.id.clear;
import static com.eggplant.admin.scipopplatform.R.id.commit;
import static com.eggplant.admin.scipopplatform.R.id.content_edit;
import static com.eggplant.admin.scipopplatform.R.id.save;
import static com.eggplant.admin.scipopplatform.R.id.title_edit;
import static com.eggplant.admin.scipopplatform.AdminUtils.*;
import static com.eggplant.admin.scipopplatform.Configure.*;

public class baseAdd extends AppCompatActivity {
    private Handler handler;


    public SharedPreferences sharedPreferences;
    /*
    存储基地信息
     */
    public static final String PREFERENCE_NAME = "baseContent";

    private EditText basenameAdd;
    private EditText basecontentAdd;
    private EditText baseaddressAdd;
    private EditText basenumberAdd;

    private Button baseSave;
    private Button baseAdd;
    private Button baseClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_add);
        bindView();
        loadMemory();

        //清除按下后弹出一个对话框
        baseClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(baseAdd.this);
                dialog.setIcon(R.mipmap.sci_pop);
                dialog.setTitle("确认清除介绍内容么");
                dialog.setPositiveButton("清除内容", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        basecontentAdd.setText("");
                    }
                });
                dialog.setNegativeButton("取消清除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
        });
        //保存
        baseSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", basenameAdd.getText().toString());
                editor.putString("content", basecontentAdd.getText().toString());
                editor.putString("address", baseaddressAdd.getText().toString());
                editor.putString("number", basenumberAdd.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
            }
        });

        //提交到服务端
        baseAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String basename = basenameAdd.getText().toString();
                String content = basecontentAdd.getText().toString();
                String baseAddress = baseaddressAdd.getText().toString();
                String baseNumber = basenumberAdd.getText().toString();
                SharedPreferences namePreferences = getSharedPreferences(USER_PREFERENCE_NAME, MODE_PRIVATE);
                String username = namePreferences.getString("name", "");
                baseAdd(basename, content, baseAddress, baseNumber, username, handler);
            }
        });
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        Toast.makeText(getApplicationContext(), "成功提交", Toast.LENGTH_SHORT).show();
                        break;
                    case NO_RIGHT:
                        Toast.makeText(getApplicationContext(), "权限不足，提交失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    void bindView() {
        basenameAdd = (EditText)findViewById(R.id.basename_add);
        baseaddressAdd = (EditText)findViewById(R.id.baseaddress_add);
        basenumberAdd = (EditText)findViewById(R.id.basenumber_add);
        basecontentAdd = (EditText)findViewById(R.id.basecontent_add);
        baseAdd = (Button)findViewById(R.id.basecommit);
        baseSave = (Button)findViewById(R.id.basesave);
        baseClear = (Button)findViewById(R.id.baseclear);
    }
    void loadMemory() {
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE);
        if (sharedPreferences.getString("title", "").equals("") && sharedPreferences.getString("content", "").equals("")) {}
        else {
            basenameAdd.setText(sharedPreferences.getString("name", ""));
            basecontentAdd.setText(sharedPreferences.getString("content", ""));
            baseaddressAdd.setText(sharedPreferences.getString("address", ""));
            basenumberAdd.setText(sharedPreferences.getString("number", ""));
        }
    }
}
