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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import static android.R.attr.data;
import static com.eggplant.admin.scipopplatform.AdminUtils.baseNameList;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_CODE;
import static com.eggplant.admin.scipopplatform.R.id.baseChoose;
import static com.eggplant.admin.scipopplatform.R.id.clear;
import static com.eggplant.admin.scipopplatform.R.id.commit;
import static com.eggplant.admin.scipopplatform.R.id.content_edit;
import static com.eggplant.admin.scipopplatform.AdminUtils.*;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.R.id.title_edit;

public class proUpdate extends AppCompatActivity {

    private Handler basehandler;
    private Handler updatehandler;

    public SharedPreferences namePreferences;

    private EditText title_upadte;
    private EditText content_update;
    private Button update;
    private Button update_clear;
    private Spinner Update_baseChoose;

    private String infoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_update);

        bindView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        infoId = bundle.getString("infoId");
        String title = bundle.getString("title");
        String content = bundle.getString("content");
        title_upadte.setText(title);
        content_update.setText(content);



        //清除按下后弹出一个对话框
        update_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(proUpdate.this);
                dialog.setIcon(R.mipmap.sci_pop);
                dialog.setTitle("确认清除么");
                dialog.setPositiveButton("清除内容", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        content_update.setText("");
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
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = title_upadte.getText().toString();
                String content = content_update.getText().toString();
                String baseId = baseIdList.get(Update_baseChoose.getSelectedItemPosition());
                namePreferences = getSharedPreferences(USER_PREFERENCE_NAME, MODE_PRIVATE);
                String name = namePreferences.getString("name", "");
                proUpdate(infoId, title, content, baseId, name, updatehandler);
            }
        });
        updatehandler = new Handler() {
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


        basehandler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(proUpdate.this, android.R.layout.simple_spinner_item, baseNameList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Update_baseChoose.setAdapter(adapter);
                        break;
                    default:
                        break;
                }
            }
        };
        loadBase(basehandler);
    }

    void bindView() {
        title_upadte = (EditText)findViewById(R.id.title_upadte);
        content_update = (EditText)findViewById(R.id.content_update);
        update = (Button)findViewById(R.id.update);
        update_clear = (Button)findViewById(R.id.update_clear);
        Update_baseChoose = (Spinner)findViewById(R.id.Update_baseChoose);
    }
}
