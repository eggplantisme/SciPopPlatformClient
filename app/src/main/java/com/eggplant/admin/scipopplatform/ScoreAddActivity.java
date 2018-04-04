package com.eggplant.admin.scipopplatform;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.eggplant.admin.scipopplatform.AdminUtils.baseIdList;
import static com.eggplant.admin.scipopplatform.AdminUtils.baseNameList;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.*;

public class ScoreAddActivity extends AppCompatActivity {
    private Handler handler;

    private EditText nameToAdd;
    private Button addScore;

    private void addScoreToUser(final String name) {
        //服务端
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = SERVER + "/addScore/" + name;
                    String res = Connect(url, null, GET);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                        handler.sendMessage(message);
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        message.what = jsonObject.getInt("info");
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_add);
        bindView();
        addScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameToAdd.getText().toString();
                addScoreToUser(name);
            }
        });

        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case NO_RIGHT:
                        Toast.makeText(getApplicationContext(), "权限不足", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        Toast.makeText(getApplicationContext(), "加分成功", Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_NAME:
                        Toast.makeText(getApplicationContext(), "名字不存在", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void bindView() {
        nameToAdd = (EditText)findViewById(R.id.nameToAdd);
        addScore = (Button)findViewById(R.id.addScore);
    }
}
