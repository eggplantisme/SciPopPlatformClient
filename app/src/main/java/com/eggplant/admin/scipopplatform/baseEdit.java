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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;
import static com.eggplant.admin.scipopplatform.AdminUtils.baseDelete;
import static com.eggplant.admin.scipopplatform.AdminUtils.proDelete;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.Connect;
import static com.eggplant.admin.scipopplatform.HttpHelper.GET;
import static com.eggplant.admin.scipopplatform.R.id.proList;

public class baseEdit extends AppCompatActivity {

    private PullRefreshLayout refreshBaseList;
    private ListView baseList;
    private String name;

    private Handler loadListhandler;
    private Handler deleteHandler;
    /*
    加载到列表中的数据
     */
    private JSONArray ListData;
    final private List<Map<String, Object>> data = new ArrayList<>();

    /*
    获取登陆的基地会员的全部基地
     */
    private void loadList(final String name) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String url = getResources().getString(R.string.server) + "/getSciBaseList/" + name;
                    String res = Connect(url, null, GET);
                    Message message = Message.obtain(loadListhandler);
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
                            temp.put("baseId", jsonObject.get("baseId"));
                            temp.put("baseName", jsonObject.get("baseName"));
                            temp.put("number", jsonObject.get("contactNumber"));
                            temp.put("baseInfo", jsonObject.get("baseInfo").toString());
                            temp.put("address", jsonObject.get("address"));

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
        setContentView(R.layout.activity_base_edit);

        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFERENCE_NAME, MODE);
        name = sharedPreferences.getString("name", "");

        bindView();

        loadList(name);
        loadListhandler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(getApplicationContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.base_edit_item,
                                new String[] {"baseId", "baseName", "number", "address"}, new int[] {R.id.baseId, R.id.baseName, R.id.baseNumber, R.id.baseAddress});
                        baseList.setAdapter(simpleAdapter);

                        refreshBaseList.setRefreshing(false);
                        break;
                    default:
                        break;
                }
            }
        };

        /*
        短按编辑，长按删除
         */
        baseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("baseId", data.get(position).get("baseId").toString());
                bundle.putString("baseName", data.get(position).get("baseName").toString());
                bundle.putString("baseInfo", data.get(position).get("baseInfo").toString());
                bundle.putString("baseNumber", data.get(position).get("number").toString());
                bundle.putString("baseAddress", data.get(position).get("address").toString());
                Intent intent = new Intent(baseEdit.this, baseUpdate.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        baseList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int p = position;
                AlertDialog.Builder dialog = new AlertDialog.Builder(baseEdit.this);
                dialog.setIcon(R.mipmap.sci_pop);
                dialog.setTitle("删除么");
                dialog.setPositiveButton("同意删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String baseIdtoDelete = data.get(p).get("baseId").toString();
                        baseDelete(baseIdtoDelete, deleteHandler);
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
        refreshBaseList.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList(name);
            }
        });
    }

    void bindView() {
        refreshBaseList = (PullRefreshLayout)findViewById(R.id.refreshBaseList);
        baseList = (ListView)findViewById(R.id.baseList);
    }
}
