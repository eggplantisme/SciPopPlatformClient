package com.eggplant.admin.scipopplatform;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.R.id.message;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.*;
import static com.eggplant.admin.scipopplatform.AdminUtils.*;

import java.sql.Date;
public class proAdd extends AppCompatActivity {
    private Handler basehandler;
    private Handler commithandler;

    public SharedPreferences sharedPreferences;
    public static final String PREFERENCE_NAME = "proContent";


    public SharedPreferences namePreferences;

    private Button commit;
    private Button save;
    private Button clear;
    private EditText title_edit;
    private EditText content_edit;
    private Spinner baseChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_add);
        bindView();
        loadMemory();

        //清除按下后弹出一个对话框
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(proAdd.this);
                dialog.setIcon(R.mipmap.sci_pop);
                dialog.setTitle("确认清除么");
                dialog.setPositiveButton("清除内容", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        content_edit.setText("");
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

        //提交到服务端
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = title_edit.getText().toString();
                String content = content_edit.getText().toString();
                String baseId = baseIdList.get(baseChoose.getSelectedItemPosition());
                namePreferences = getSharedPreferences(USER_PREFERENCE_NAME, MODE_PRIVATE);
                String name = namePreferences.getString("name", "");
                proAdd(title, content, baseId, name, commithandler);
            }
        });
        commithandler = new Handler() {
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

        //保存
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("title", title_edit.getText().toString());
                editor.putString("content", content_edit.getText().toString());
                editor.commit();
            }
        });


        basehandler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(proAdd.this, android.R.layout.simple_spinner_item, baseNameList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        baseChoose.setAdapter(adapter);
                        break;
                    default:
                        break;
                }
            }
        };
        loadBase(basehandler);
    }

    void bindView() {
        commit = (Button)findViewById(R.id.commit);
        save = (Button)findViewById(R.id.save);
        clear = (Button)findViewById(R.id.clear);
        title_edit = (EditText)findViewById(R.id.title_edit);
        content_edit = (EditText)findViewById(R.id.content_edit);
        baseChoose = (Spinner)findViewById(R.id.baseChoose);
    }

    void loadMemory() {
        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE);
        if (sharedPreferences.getString("title", "").equals("") && sharedPreferences.getString("content", "").equals("")) {}
        else {
            title_edit.setText(sharedPreferences.getString("title", ""));
            content_edit.setText(sharedPreferences.getString("content", ""));
        }
    }
}
