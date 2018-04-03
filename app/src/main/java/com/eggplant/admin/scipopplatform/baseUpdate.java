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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.R.attr.data;
import static com.eggplant.admin.scipopplatform.AdminUtils.baseIdList;

import static com.eggplant.admin.scipopplatform.Configure.*;

import static com.eggplant.admin.scipopplatform.AdminUtils.*;

public class baseUpdate extends AppCompatActivity {

    private Handler baseUpdateHandler;

    private SharedPreferences sharedPreferences;

    private EditText basenameUpdate;
    private EditText basecontentUpdate;
    private EditText baseaddressUpdate;
    private EditText basenumberUpdate;

    private Button baseUpdate;
    private Button baseClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_update);
        bindView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        final String baseId = bundle.getString("baseId");
        String baseName = bundle.getString("baseName");
        String baseInfo = bundle.getString("baseInfo");
        String baseNumber = bundle.getString("baseNumber");
        String baseAddress = bundle.getString("baseAddress");
        basenameUpdate.setText(baseName);
        basecontentUpdate.setText(baseInfo);
        baseaddressUpdate.setText(baseAddress);
        basenumberUpdate.setText(baseNumber);

        //清除按下后弹出一个对话框
        baseClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(baseUpdate.this);
                dialog.setIcon(R.mipmap.sci_pop);
                dialog.setTitle("确认清除么");
                dialog.setPositiveButton("清除内容", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        basecontentUpdate.setText("");
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
        baseUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String basename = basenameUpdate.getText().toString();
                String baseinfo = basecontentUpdate.getText().toString();
                String baseaddress = baseaddressUpdate.getText().toString();
                String basenumber = basenumberUpdate.getText().toString();
                sharedPreferences = getSharedPreferences(USER_PREFERENCE_NAME, MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "");
                baseUpdate(baseId, basename, baseinfo, baseaddress, basenumber, name, baseUpdateHandler);
            }
        });
        baseUpdateHandler = new Handler() {
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

    private void bindView() {
        basenameUpdate = (EditText)findViewById(R.id.basename_update);
        basecontentUpdate = (EditText)findViewById(R.id.basecontent_update);
        baseaddressUpdate = (EditText)findViewById(R.id.baseaddress_update);
        basenumberUpdate = (EditText)findViewById(R.id.basenumber_update);
        baseUpdate = (Button)findViewById(R.id.basecommit_update);
        baseClear = (Button)findViewById(R.id.baseclear_update);
    }
}
