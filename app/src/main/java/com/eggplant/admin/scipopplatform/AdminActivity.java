package com.eggplant.admin.scipopplatform;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.*;

public class AdminActivity extends AppCompatActivity {
    private Handler handler;
    private Handler refreshHandler;


    private ListView TempUserList;
    private Toolbar adminTop;

    private String tempUserList;
    final private List<Map<String, Object>> data = new ArrayList<>();

    private PullRefreshLayout pullRefreshLayout;

    /*
    将JsonArray 字符串转化为 data
     */
    private void JsonStringToData(String res) {
        try {
            data.clear();
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("name", jsonObject.getString("username"));
                map.put("phone", jsonObject.getString("phone"));
                map.put("userclass", jsonObject.getInt("userclass"));
                data.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    重新加载数据
     */
    private void refreshConnect() {
        final String url = getResources().getString(R.string.server) + "/adminList";
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int info = NOT_ADMIN;
                try {
                    String res = Connect(url, null, GET);
                    if (res != null) {
                        tempUserList = res;
                        info = RIGHT;
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

                Message message = Message.obtain(refreshHandler);
                message.what = info;
                message.sendToTarget();
            }
        };
        new Thread(runnable).start();
    }

    /*
    统一或者删除的申请
     */
    private void agreeOrDeleteConnect(final String url) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int info = NOT_ADMIN;
                try {
                    JSONObject jsonObject = new JSONObject(Connect(url, null, GET));
                    info = jsonObject.getInt("info");
                }catch (Exception e) {
                    e.printStackTrace();
                }
                Message message = Message.obtain(handler);
                message.what = info;
                message.sendToTarget();
            }
        };
        new Thread(runnable).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //获取待检查用户信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        tempUserList = bundle.getString("tempUserList");

        bindView();
        loadList(tempUserList);

        adminTop.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO 管理员的其他功能
                return false;
            }
        });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshConnect();
            }
        });


        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case NOT_ADMIN:
                        Toast.makeText(getApplicationContext(), "权限不足", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        refreshConnect();
                        break;
                    default:
                        break;
                }
            }
        };

        refreshHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NOT_ADMIN:
                        Toast.makeText(getApplicationContext(), "权限不足", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        refresh();
                        pullRefreshLayout.setRefreshing(false);
                        break;
                    default:
                        break;
                }
            }
        };
        /*
        支持右上角menu
         */
        setSupportActionBar(adminTop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    public void bindView() {
        TempUserList = (ListView)findViewById(R.id.TempUserList);
        adminTop = (Toolbar) findViewById(R.id.adminTop);
        pullRefreshLayout = (PullRefreshLayout)findViewById(R.id.admin_refresh);
    }
    /*
    刷新列表
     */
    private void refresh() {
        loadList(tempUserList);
    }

    /*
    将数据加载到列表中
     */
    public void loadList(String listData) {
        try {
            JsonStringToData(listData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("data", data.toString());
        SimpleAdapter simpleAdapter = new SimpleAdapter(AdminActivity.this, data, R.layout.admin_item, new String[]{"name", "phone", "userclass"}, new int[] {R.id.tempUserName, R.id.tempUserPhone, R.id.tempUserClass});
        TempUserList.setAdapter(simpleAdapter);

        /*
        选择同意，删除，或者取消
         */
        TempUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String name = data.get(position).get("name").toString();

                AlertDialog.Builder dialog = new AlertDialog.Builder(AdminActivity.this);
                dialog.setIcon(R.mipmap.sci_pop);
                dialog.setTitle("选择操作");
                dialog.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = getResources().getString(R.string.server) + "/adminAgree/" + name;
                        agreeOrDeleteConnect(url);
                    }
                });
                dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = getResources().getString(R.string.server) + "/adminDelete/" + name;
                        agreeOrDeleteConnect(url);
                    }
                });
                dialog.show();
            }
        });



    }
}
