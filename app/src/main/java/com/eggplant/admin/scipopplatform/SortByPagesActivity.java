package com.eggplant.admin.scipopplatform;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.R.id.message;
import static com.eggplant.admin.scipopplatform.Configure.NOT_ADMIN;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.*;
import static com.eggplant.admin.scipopplatform.AdminUtils.*;
import static com.eggplant.admin.scipopplatform.R.id.TempUserList;

public class SortByPagesActivity extends AppCompatActivity {
    private Handler handler;

    private ListView sort;
    final private List<Map<String, Object>> data = new ArrayList<>();


    void loadListData() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int info = UNKNOWN_WRONG;
                String url = getResources().getString(R.string.server) + "/adminProList";
                try {
                    String res = Connect(url, null, GET);
                    if (res != null) {
                        JSONArray jsonArray = new JSONArray(res);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String name = jsonArray.getJSONObject(i).getString("username");
                            List<Map<String, Object>> pagesdata = new ArrayList<>();
                            loadProPageList(name, null, pagesdata);
                            int pageNumber = pagesdata.size();
                            Map<String, Object> map = new LinkedHashMap<>();
                            map.put("name", name);
                            map.put("pageNumber", pageNumber);
                            data.add(map);
                            info = RIGHT;
                        }
                        /*
                        排序
                         */
                        Collections.sort(data, new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                                if (Integer.parseInt(o1.get("pageNumber").toString()) < Integer.parseInt(o2.get("pageNumber").toString())) {
                                    return 1;
                                } else if (Integer.parseInt(o1.get("pageNumber").toString()) == Integer.parseInt(o2.get("pageNumber").toString())) {
                                    return 0;
                                } else  {
                                    return -1;
                                }
                            }
                        });

                    }
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
        setContentView(R.layout.activity_sort_by_pages);

        sort = (ListView)findViewById(R.id.sort);

        loadListData();
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case NOT_ADMIN:
                        Toast.makeText(getApplicationContext(), "权限不足获网络连接出错", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        SimpleAdapter simpleAdapter = new SimpleAdapter(SortByPagesActivity.this, data, R.layout.sort_item, new String[]{"name", "pageNumber"}, new int[] {R.id.proAdminName, R.id.pages});
                        sort.setAdapter(simpleAdapter);
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
