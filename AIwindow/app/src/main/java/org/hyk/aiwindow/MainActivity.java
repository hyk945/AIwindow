package org.hyk.aiwindow;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    //ViewPager界面
    private List<Fragment> FragmentList = new ArrayList<>();
    private bluetoothFragment bluetoothFg;
    private canshuFragment canshuFg;
    private otherFragment otherFg;
    private FragmentAdapter fragmentadapter;
    private ViewPager viewPager;

    private ImageView imageline;
    private TextView blue_textview;
    private TextView canshu_textview;
    private TextView other_textview;

    private int currentIndex = 0;
    private int screenwidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findById();
        init();
        initTabLineWidth();


    }

    private void findById() {

        blue_textview = (TextView) findViewById(R.id.bluetooth);
        canshu_textview = (TextView) findViewById(R.id.canshu);
        other_textview = (TextView) findViewById(R.id.other);
        imageline = (ImageView) findViewById(R.id.imageline);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        blue_textview.setOnClickListener(this);
        canshu_textview.setOnClickListener(this);
        other_textview.setOnClickListener(this);


    }

    private void init() {
        bluetoothFg = new bluetoothFragment();
        canshuFg = new canshuFragment();
        otherFg = new otherFragment();
        FragmentList.add(bluetoothFg);
        FragmentList.add(canshuFg);
        FragmentList.add(otherFg);

        fragmentadapter = new FragmentAdapter(this.getSupportFragmentManager(), FragmentList);
        viewPager.setAdapter(fragmentadapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int positionOffsetPixels) {

                LinearLayout.LayoutParams imagelp = (LinearLayout.LayoutParams) imageline.getLayoutParams();

                Log.d("111", String.valueOf(position));
                if (currentIndex == 0 && position == 0) {//碎片位置0-1的滑动过程，下同
                    imagelp.leftMargin = (int) (offset * (screenwidth * 1.0 / 3) + currentIndex
                            * (screenwidth / 3));

                } else if (currentIndex == 1 && position == 0) {       //1->0
                    imagelp.leftMargin = (int) (
                            -(1 - offset) * (screenwidth * 1.0 / 3) + currentIndex
                                    * (screenwidth / 3));

                } else if (currentIndex == 1 && position == 1) {         // 1->2
                    imagelp.leftMargin = (int) (offset * (screenwidth * 1.0 / 3) + currentIndex
                            * (screenwidth / 3));

                } else if (currentIndex == 2 && position == 1) {          // 2->1
                    imagelp.leftMargin = (int) (-(1 - offset)
                            * (screenwidth * 1.0 / 3) + currentIndex
                            * (screenwidth / 3));

                }

                imageline.setLayoutParams(imagelp);
            }

            @Override
            public void onPageSelected(int position) {

                resetTextViewcolor();
                switch (position) {
                    case 0:
                        blue_textview.setTextColor(Color.BLUE);
                        break;
                    case 1:
                        canshu_textview.setTextColor(Color.BLUE);
                        break;
                    case 2:
                        other_textview.setTextColor(Color.BLUE);
                        break;
                }
                currentIndex = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    //重置颜色
    private void resetTextViewcolor() {
        blue_textview.setTextColor(Color.BLACK);
        canshu_textview.setTextColor(Color.BLACK);
        other_textview.setTextColor(Color.BLACK);
    }

    //设置滑动条的宽度为屏幕的1/3
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        screenwidth = dpMetrics.widthPixels;

        LinearLayout.LayoutParams imagelp = (LinearLayout.LayoutParams) imageline.getLayoutParams();
        imagelp.width = screenwidth / 3;
        imageline.setLayoutParams(imagelp);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetooth:
                viewPager.setCurrentItem(0);
                break;
            case R.id.canshu:
                viewPager.setCurrentItem(1);
                break;
            case R.id.other:
                viewPager.setCurrentItem(2);
                break;
            case R.id.scan:

            default:
                break;

        }
    }
}
