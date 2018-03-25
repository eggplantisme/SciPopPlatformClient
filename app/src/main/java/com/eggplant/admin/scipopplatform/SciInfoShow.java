package com.eggplant.admin.scipopplatform;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Date;

import static android.R.attr.id;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.Connect;
import static com.eggplant.admin.scipopplatform.HttpHelper.GET;

public class SciInfoShow extends AppCompatActivity {
    private Handler handler;
    private String Infoid;
    JSONObject response;

    private TextView title;
    private TextView main_info;
    private Button name;
    private Button address;

    private Date lastDate;
    private int baseId;

    protected void connect(final String Infoid) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = getResources().getString(R.string.server) + "/getSciPopInfo/" + Infoid;
                String responseData = Connect(url, null, GET);
                try {
                    response = new JSONObject(responseData);
                } catch (Exception e){
                    e.printStackTrace();
                }
                Message message = Message.obtain(handler);
                if (responseData == null) {
                    message.what = WRONG_CODE;
                    message.sendToTarget();
                } else {
                    message.what = RIGHT;
                    message.sendToTarget();
                }
            }
        };
        new Thread(runnable).start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sci_info_show);
        loadView();
        /*
        获得传入的id，便于网络访问
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Infoid = bundle.getString("id");
        connect(Infoid);

        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        try {
                            title.setText(response.getString("title"));
                            main_info.setText(response.getString("content"));
                            name.setText(response.getString("writterName") + "\n" + response.getString("lastTime"));
                            address.setText("前往基地详情");
                            baseId = response.getInt("baseId");
                            //TODO 根据baseId加载基地名字，然后跳转到基地详情页面
                            address.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle =  new Bundle();
                                    bundle.putString("id", String.valueOf(baseId));
                                    Intent intent = new Intent(SciInfoShow.this, SciBaseShow.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    void loadView() {
        title = (TextView)findViewById(R.id.title);
        main_info = (TextView)findViewById(R.id.main_info);
        name = (Button)findViewById(R.id.name);
        address = (Button)findViewById(R.id.address);
    }

}
