package com.eggplant.admin.scipopplatform;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import static android.R.attr.id;

import static com.eggplant.admin.scipopplatform.Configure.*;


public class MainActivity extends AppCompatActivity {
    public SharedPreferences sharedPreferences;


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
                        //TODO 积分功能
                        Toast.makeText(MainActivity.this, "积分功能暂时没做", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sign_out:
                        //清除数据
                        sharedPreferences.edit().clear().commit();
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        MainActivity.this.startActivity(intent);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        /*
        支持右上角menu
         */
        setSupportActionBar(top_title);
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
    TODO 这里只有用户姓名的获取
     至于图片和分数，电话信息服务端没有接口
     暂时搁置
     图片还没做*/
    private void loadLeftDrawer() {
        String name = sharedPreferences.getString("name", null);
        //动态加载头部
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
