package com.eggplant.admin.scipopplatform;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONObject;

import static android.R.attr.data;
import static android.R.attr.id;

import static android.R.id.message;
import static com.eggplant.admin.scipopplatform.Configure.*;
import static com.eggplant.admin.scipopplatform.HttpHelper.Connect;
import static com.eggplant.admin.scipopplatform.HttpHelper.GET;


public class MainActivity extends AppCompatActivity {
    public SharedPreferences sharedPreferences;

    private Handler Scorehandler;
    private int Score;


    private TextView sci_info;
    private TextView sci_base;
    private Toolbar top_title;
    private DrawerLayout drawerLayout;
    private NavigationView leftDrawer;

    private FrameLayout main_content;

    private MainFragment sciInfoFrag, sciBaseFrag;

    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(USER_PREFERENCE_NAME, MODE);

        fragmentManager = getFragmentManager();
        bindViews();
        loadLeftDrawer();

        /*
        初始获取科普信息的列表
         */
        getSciInfoList();

        sci_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSciInfoList();
            }
        });

        sci_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSciBaseList();
            }
        });

        /*
        显示侧滑菜单
         */
        top_title.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });



        /*
        侧滑菜单具体内容
         */
        leftDrawer.setItemIconTintList(null);//为了图标显示
        leftDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.score:
                        String name = sharedPreferences.getString("name", null);
                        getScore(name);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        Scorehandler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case NO_RIGHT:
                        Toast.makeText(getApplicationContext(), "权限不足", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setIcon(R.mipmap.sci_pop);
                        dialog.setTitle("分数：" + Score);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.show();
                        break;
                    default:
                        break;
                }
            }
        };
        /*
        支持右上角menu
         */
        setSupportActionBar(top_title);
    }

    private void getScore(final String name) {
        Score = 0;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int info = NO_RIGHT;
                try {
                    String url = SERVER + "/getScore/" + name;
                    String res = Connect(url, null, GET);
                    Message message = Message.obtain(Scorehandler);
                    if (res == null) {
                        message.what = UNKNOWN_WRONG;
                    } else {
                        JSONObject jsonObject = new JSONObject(res);
                        info = jsonObject.getInt("info");
                        if (info == RIGHT) {
                            Score = jsonObject.getInt("score");
                        } else {
                            info = NO_RIGHT;
                        }
                    }
                    message.what = info;
                    Scorehandler.sendMessage(message);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    /*
    根据用户类别选择不同menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        int userClass = sharedPreferences.getInt("class", 0);//如果没有class， 默认给0
        switch (userClass) {
            case NORMAL:
                break;
            case PROFESSION:
                getMenuInflater().inflate(R.menu.pro_main_menu, menu);
                break;
            case BASE:
                getMenuInflater().inflate(R.menu.base_main_menu, menu);
                break;
            default:
                break;
        }
        /*
        右上角按钮点击
        */
        top_title.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int userClass = sharedPreferences.getInt("class", 0);//如果没有class， 默认给0
                Log.v("class", String.valueOf(userClass));
                Intent intent;
                switch (userClass) {
                    case NORMAL:
                        break;
                    case PROFESSION:
                        switch (item.getItemId()) {
                            case R.id.pro_create:
                                intent = new Intent(MainActivity.this, proAdd.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            case R.id.pro_change:
                                intent = new Intent(MainActivity.this, proEdit.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            case R.id.pro_delete:
                                intent = new Intent(MainActivity.this, proEdit.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        break;
                    case BASE:
                        switch (item.getItemId()) {
                            case R.id.base_create:
                                intent = new Intent(MainActivity.this, baseAdd.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            case R.id.base_change:
                                intent = new Intent(MainActivity.this, baseEdit.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            case R.id.base_delete:
                                intent = new Intent(MainActivity.this, baseEdit.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            case R.id.add_score:
                                intent = new Intent(MainActivity.this, ScoreAddActivity.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        return true;
    }



    /*
    加载当前登陆用户的名字*/
    private void loadLeftDrawer() {
        String name = sharedPreferences.getString("name", null);
        View headerLayout = leftDrawer.inflateHeaderView(R.layout.drawer_head);
        TextView left_draw_name = (TextView)headerLayout.findViewById(R.id.person_name);
        left_draw_name.setText(name);
    }

    private void bindViews() {
        sci_info = (TextView)findViewById(R.id.sciInfo);
        sci_base = (TextView)findViewById(R.id.sciBase);
        main_content = (FrameLayout)findViewById(R.id.main_content);
        top_title = (Toolbar)findViewById(R.id.top_barText);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        leftDrawer = (NavigationView)findViewById(R.id.left_drawer);

    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (sciInfoFrag != null) fragmentTransaction.hide(sciInfoFrag);
        if (sciBaseFrag != null) fragmentTransaction.hide(sciBaseFrag);
    }

    private void resetSelect() {
        sci_info.setSelected(false);
        sci_base.setSelected(false);
    }

    private void getSciInfoList() {
        top_title.setTitle("科普信息");

        resetSelect();
        sci_info.setSelected(true);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (sciInfoFrag == null) {
            sciInfoFrag = new MainFragment(getResources().getString(R.string.server) + "/getTitleList", SCIINFO);
            fragmentTransaction.add(R.id.main_content, sciInfoFrag);
        } else {
            sciInfoFrag.refresh();
            fragmentTransaction.show(sciInfoFrag);
        }
        fragmentTransaction.commit();
    }
    private void getSciBaseList() {
        top_title.setTitle("基地信息");
        resetSelect();
        sci_base.setSelected(true);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (sciBaseFrag == null) {
            sciBaseFrag = new MainFragment(getResources().getString(R.string.server) + "/getBaseList", SCIBASE);
            fragmentTransaction.add(R.id.main_content, sciBaseFrag);
        } else {
            sciBaseFrag.refresh();
            fragmentTransaction.show(sciBaseFrag);
        }
        fragmentTransaction.commit();
    }
}
