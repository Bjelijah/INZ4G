package com.inz.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


import com.inz.action.PlayBackVideoListAction;
import com.inz.activity.fragment.VodFragment;
import com.inz.bean.CameraItemBean;
import com.inz.datetime.JudgeDate;
import com.inz.datetime.ScreenInfo;
import com.inz.datetime.WheelMain;
import com.inz.inz4g.R;
import com.inz.utils.IConfig;
import com.inz.utils.PhoneConfig;
import com.inz.utils.ScaleImageUtils;
import com.inz.utils.Util;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/1/3.
 */

public class VideoListActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,IConfig{
    private static final int MSG_VIDEO_LIST_DATA_UPDATE    = 0x00;


    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    FloatingActionButton mFab;
    Toolbar mTb;
    AppBarLayout mAppbar;
    ImageView mBk;
    CollapsingToolbarLayout mCollapsing;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    CameraItemBean mBean;
    int imageWidth,imageHeight;
    VodFragment mFragment;
    private com.inz.datetime.WheelMain wheelMain;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();// set bean before fragment
        setContentView(R.layout.activity_video_list);
        initView();
    }

    private void init(){
        imageWidth = PhoneConfig.getPhoneWidth(this)/2;
        imageHeight = imageWidth * 10 / 16;
        mBean = (CameraItemBean) getIntent().getSerializableExtra("bean");
        boolean isFromPlayView = getIntent().getBooleanExtra("playview",false);

        if(IS_TEST){
            mBean.setStore(true);
        }
        Log.i("123","~~~~~~~~~~~~store="+mBean.isStore());
        PlayBackVideoListAction.getInstance().init(this,mBean).setNeedInitDelay(isFromPlayView);
    }

    @Override
    protected void onDestroy() {
        PlayBackVideoListAction.getInstance().deInit();
        super.onDestroy();
    }

    private void initView(){
        mFab = (FloatingActionButton) findViewById(R.id.listview_fab);
        mFab.setImageDrawable(new IconicsDrawable(this,GoogleMaterial.Icon.gmd_search).actionBar().color(Color.WHITE));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelTimeFun();
            }
        });
        mCollapsing = (CollapsingToolbarLayout) findViewById(R.id.videolist_collapsing);
        mCollapsing.setTitle(mBean.getCameraName());
        mBk = (ImageView) findViewById(R.id.videolist_backdrop);
        Bitmap bm = null;
        try {
            bm = ScaleImageUtils.decodeFile(imageWidth, imageHeight, new File(mBean.getPicturePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(bm == null){
            mBk.setImageResource(R.mipmap.card_camera_default_image2);
//            mBk.setImageBitmap(null);
//            mBk.setBackgroundColor(this.getResources().getColor(R.color.item_camera_video));
        }else{
            mBk.setImageBitmap(bm);
        }

        mTb = (Toolbar) findViewById(R.id.videolist_toolbar);
        mTb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAppbar = (AppBarLayout) findViewById(R.id.videolist_appbar);
        mAppbar.addOnOffsetChangedListener(this);

        mFragment = (VodFragment) getSupportFragmentManager().findFragmentById(R.id.videolist_fragment);
        mFragment.setBean(mBean);
    }

    private void wheelTimeFun(){
        final View timepickerview= LayoutInflater.from(VideoListActivity.this).inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(VideoListActivity.this);
        String country = getResources().getConfiguration().locale.getCountry();
        wheelMain = new WheelMain(timepickerview,country);
        wheelMain.screenheight = screenInfo.getHeight();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String time = (calendar.get(Calendar.YEAR) + "-" +
                (calendar.get(Calendar.MONTH) + 1 )+ "-" +
                calendar.get(Calendar.DAY_OF_MONTH) + "");
        if(JudgeDate.isDate(time, "yyyy-MM-dd")){
            try {
                calendar.setTime(dateFormat.parse(time));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int  month = calendar.get(Calendar.MONTH) ;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        wheelMain.initDateTimePicker(year,month,day);
        new AlertDialog.Builder(VideoListActivity.this)
                .setTitle(getResources().getString(R.string.select_date))
                .setView(timepickerview)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//						now.setText(wheelMain.getTime());
                        //"yyyy-MM-dd'T'HH:mm:ss"
                        String endTime = wheelMain.getEndTime();
                        String startTime = wheelMain.getStartTIme(endTime,1);
                        Log.i("123","!!!!!!!!!!!!!endTime="+endTime+"  startTime="+startTime);
                        mFragment.searchList(startTime,endTime);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        }
    }
}
