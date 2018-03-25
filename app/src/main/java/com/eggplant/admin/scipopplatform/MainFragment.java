package com.eggplant.admin.scipopplatform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.id;
import static android.R.id.message;
import static android.media.CamcorderProfile.get;
import static com.eggplant.admin.scipopplatform.Configure.RIGHT;
import static com.eggplant.admin.scipopplatform.Configure.SCIBASE;
import static com.eggplant.admin.scipopplatform.Configure.SCIINFO;
import static com.eggplant.admin.scipopplatform.Configure.WRONG_CODE;
import static com.eggplant.admin.scipopplatform.HttpHelper.COOKIE;
import static com.eggplant.admin.scipopplatform.HttpHelper.Connect;
import static com.eggplant.admin.scipopplatform.HttpHelper.GET;

/**
 * Created by admin on 2018/3/13.
 */

@SuppressLint("ValidFragment")
public class MainFragment extends Fragment {
    private Handler handler;

    private String url;
    private int fragStyle;
    private JSONArray responseData = null;

    private Activity context;

    private PullRefreshLayout pullRefreshLayout;

    public MainFragment(String url, int FragmentStyle) {
        this.url = url;
        this.fragStyle = FragmentStyle;
    }
    protected void connect(final String url) {
        /*
        网络连接的线程
         */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    responseData = new JSONArray(Connect(url, null, GET));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.main_fragment_style,container,false);
        context = getActivity();
        final ListView mainList = (ListView)view.findViewById(R.id.mainList);
        connect(url);
        /*
        TextView txt_content = (TextView)view.findViewById(R.id.txt_content);
        txt_content.setText(url);
        txt_content.setBackgroundColor(0xFFFF0000);
        */
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case WRONG_CODE:
                        Toast.makeText(context, "网络连接错误", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        loadList(view.getContext(), mainList, responseData);
                        Toast.makeText(context, "加载数据成功", Toast.LENGTH_SHORT).show();
                        pullRefreshLayout.setRefreshing(false);
                        break;
                    default:
                        break;
                }
            }
        };

        pullRefreshLayout = (PullRefreshLayout)view.findViewById(R.id.main_refresh);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }
    /*
    将网络返回数据加载到列表中
     */
    protected void loadList(final Context context, ListView mainList, JSONArray responseData) {
        final List<Map<String, Object>> data = new ArrayList<>();
        try {
            for (int i = 0; i < responseData.length(); i++) {
                JSONObject jsonObject;
                jsonObject = responseData.getJSONObject(i);
                Map<String, Object> temp = new LinkedHashMap<>();
                temp.put("id", jsonObject.get("id"));
                temp.put("title", jsonObject.get("title"));
                data.add(temp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(context, data, R.layout.main_item,
            new String[] {"title"}, new int[] {R.id.title});
        mainList.setAdapter(simpleAdapter);

        mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object NavigationId = data.get(position).get("id");
                String _id = NavigationId.toString();
                Bundle bundle = new Bundle();
                bundle.putString("id", _id);
                Intent intent;
                if (fragStyle == SCIINFO) {
                    intent = new Intent(context, SciInfoShow.class);
                    intent.putExtras(bundle);
                } else if (fragStyle == SCIBASE) {
                    intent = new Intent(context, SciBaseShow.class);
                    intent.putExtras(bundle);
                } else {
                    return;
                }
                context.startActivity(intent);
            }
        });
    }
    /*
    刷新操作
    耗时操作
     */
    public void refresh() {
        connect(url);
    }

}
