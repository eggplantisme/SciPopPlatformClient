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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import static com.eggplant.admin.scipopplatform.Configure.WRONG_CODE;
import static com.eggplant.admin.scipopplatform.Configure.RIGHT;
import static com.eggplant.admin.scipopplatform.HttpHelper.*;
import static com.eggplant.admin.scipopplatform.R.id.contactNumber;
import static com.eggplant.admin.scipopplatform.R.id.main_info;

public class SciBaseShow extends AppCompatActivity {
    private Handler handler;
    private TextView basename;
    private TextView contact;
    private TextView address;
    private TextView mainInfo;

    private String baseId;

    private JSONObject response;

    void connect(final String baseId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = getResources().getString(R.string.server) + "/getSciPopBase/" + baseId;
                String responseData = Connect(url, null, GET);

                try {
                    response = new JSONObject(responseData);
                } catch (Exception e) {
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
        setContentView(R.layout.activity_sci_base_show);
        loadView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        baseId = bundle.getString("id");
        connect(baseId);

        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        try {
                            basename.setText(response.getString("baseName"));
                            mainInfo.setText(response.getString("baseInfo"));
                            contact.setText("联系方式: " + response.getString("contactNumber"));
                            address.setText("地址: " + response.getString("address"));
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
        basename = (TextView)findViewById(R.id.basename);
        contact = (TextView)findViewById(contactNumber);
        address = (TextView)findViewById(R.id.address);
        mainInfo = (TextView)findViewById(main_info);
    }

}
