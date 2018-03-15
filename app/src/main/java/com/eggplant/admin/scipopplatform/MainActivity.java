package com.eggplant.admin.scipopplatform;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.eggplant.admin.scipopplatform.Configure.SCIBASE;
import static com.eggplant.admin.scipopplatform.Configure.SCIINFO;


public class MainActivity extends AppCompatActivity {
    private TextView sci_info;
    private TextView sci_base;

    private FrameLayout main_content;

    private MainFragment sciInfoFrag, sciBaseFrag;

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        bindViews();

        sci_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSelect();
                sci_info.setSelected(true);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                hideAllFragment(fragmentTransaction);
                /*
                此处没有实现手动更新页面（比如下拉）时的行为，APP打开之后只有一次获取数据机会
                之后在做改进
                //TODO
                下同
                 */
                if (sciInfoFrag == null) {
                    sciInfoFrag = new MainFragment(getResources().getString(R.string.server) + "/getTitleList", SCIINFO);
                    fragmentTransaction.add(R.id.main_content, sciInfoFrag);
                    Toast.makeText(getApplicationContext(), "创建frag", Toast.LENGTH_SHORT).show();
                } else {
                    fragmentTransaction.show(sciInfoFrag);
                }
                fragmentTransaction.commit();
            }
        });

        sci_base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSelect();
                sci_base.setSelected(true);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                hideAllFragment(fragmentTransaction);
                if (sciBaseFrag == null) {
                    sciBaseFrag = new MainFragment(getResources().getString(R.string.server) + "/getBaseList", SCIBASE);
                    fragmentTransaction.add(R.id.main_content, sciBaseFrag);
                } else {
                    fragmentTransaction.show(sciBaseFrag);
                }
                fragmentTransaction.commit();
            }
        });
    }

    private void bindViews() {
        sci_info = (TextView)findViewById(R.id.sciInfo);
        sci_base = (TextView)findViewById(R.id.sciBase);
        main_content = (FrameLayout)findViewById(R.id.main_content);
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction) {
        if (sciInfoFrag != null) fragmentTransaction.hide(sciInfoFrag);
        if (sciBaseFrag != null) fragmentTransaction.hide(sciBaseFrag);
    }

    private void resetSelect() {
        sci_info.setSelected(false);
        sci_base.setSelected(false);
    }
}
