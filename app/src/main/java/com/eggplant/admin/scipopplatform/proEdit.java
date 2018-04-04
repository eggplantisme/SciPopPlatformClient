package com.eggplant.admin.scipopplatform;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.eggplant.admin.scipopplatform.HttpHelper.*;
import com.eggplant.admin.scipopplatform.Configure.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.eggplant.admin.scipopplatform.Configure.NO_RIGHT;
import static com.eggplant.admin.scipopplatform.Configure.RIGHT;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_CODE;
import static com.eggplant.admin.scipopplatform.HttpHelper.Connect;
import static com.eggplant.admin.scipopplatform.HttpHelper.GET;
import static com.eggplant.admin.scipopplatform.HttpHelper.POST;
import static com.eggplant.admin.scipopplatform.R.id.mainList;
import static com.eggplant.admin.scipopplatform.AdminUtils.*;

public class proEdit extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "user";
    public static final int MODE = MODE_PRIVATE;
    public SharedPreferences sharedPreferences;

    private Handler handler;
    private Handler deleteHandler;
    private ListView proList;
    private PullRefreshLayout proRefresh;
    private JSONArray ListData;

    final private List<Map<String, Object>> data = new ArrayList<>();
    private String name;

    /*
    获取登陆的专家会员的全部科普文章
     */
    private void loadList(final String name) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = getResources().getString(R.string.server) + "/getSciPopInfos/" + name;
                    String res = Connect(url, null, GET);
                    Message message = Message.obtain(handler);
                    if (res == null) {
                        message.what = WRONG_CODE;
                        message.sendToTarget();
                    } else {
                        ListData = new JSONArray(res);
                        data.clear();
                        for (int i = 0; i < ListData.length(); i++) {
                            JSONObject jsonObject;
                            jsonObject = ListData.getJSONObject(i);
                            Map<String, Object> temp = new LinkedHashMap<>();
                            temp.put("infoId", jsonObject.get("infoId"));
                            temp.put("title", jsonObject.get("title"));
                            temp.put("content", jsonObject.get("content"));
                            temp.put("lastTime", jsonObject.get("lastTime").toString());
                            loadBaseSync();
                            int index = baseIdList.indexOf(jsonObject.get("baseId").toString());
                            temp.put("baseName", baseNameList.get(index));//暂时用baseId带替baseName
                            data.add(temp);
                        }
                        message.what = RIGHT;
                        message.sendToTarget();
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
        setContentView(R.layout.activity_pro_edit);

        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE);
        name = sharedPreferences.getString("name", "");

        bindView();
        loadList(name);
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.pro_edit_item,
                                new String[] {"infoId", "title", "lastTime", "baseName"}, new int[] {R.id.infoId, R.id.infoTitle, R.id.infoTime, R.id.baseName});
                        proList.setAdapter(simpleAdapter);
                        proRefresh.setRefreshing(false);
                        break;
                    default:
                        break;
                }
            }
        };

        /*
        短按编辑，长按删除
         */
        proList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("infoId", data.get(position).get("infoId").toString());
                bundle.putString("title", data.get(position).get("title").toString());
                bundle.putString("content", data.get(position).get("content").toString());
                Intent intent = new Intent(proEdit.this, proUpdate.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        proList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int p = position;
                AlertDialog.Builder dialog = new AlertDialog.Builder(proEdit.this);
                dialog.setIcon(R.mipmap.sci_pop);
                dialog.setTitle("删除么");
                dialog.setPositiveButton("同意删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String infoIdToDelete = data.get(p).get("infoId").toString();
                        proDelete(infoIdToDelete, deleteHandler);
                    }
                });

                dialog.setNegativeButton("取消删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return true;
            }
        });
        deleteHandler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        Toast.makeText(getApplicationContext(), "成功删除", Toast.LENGTH_SHORT).show();
                        loadList(name);
                        break;
                    case NO_RIGHT:
                        Toast.makeText(getApplicationContext(), "权限不足，删除失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };

        proRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList(name);
            }
        });
    }
    private void bindView() {
        proList = (ListView)findViewById(R.id.proList);
        proRefresh = (PullRefreshLayout)findViewById(R.id.refreshProList);
    }
}
