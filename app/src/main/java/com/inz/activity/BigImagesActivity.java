package com.inz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;


import com.inz.activity.view.HackyViewPager;

import com.inz.inz4g.R;
import com.inz.utils.FileUtils;
import com.inz.utils.PhoneConfig;
import com.inz.utils.ScaleImageUtils;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import photoview.PhotoView;
import photoview.PhotoViewAttacher;


/**
 * Created by Administrator on 2017/2/8.
 */

public class BigImagesActivity extends AppCompatActivity implements View.OnClickListener, PhotoViewAttacher.OnViewTapListener,ViewPager.OnPageChangeListener{

    private int position;
    //LocalFilesActivity传过来的照片地址集合
    private ArrayList<String> mList;

    //isShown用于判断按钮是否显示的标志位
    //isScale用于判断图片是否改变分辨率的标志位
    private boolean isShown,isScale;

    private Toolbar mToolbar;
    private ImageButton mShare,mBack,mDelete,mScale;
    private FrameLayout title,bottom;
    private TextView mImagePosition;
    private HackyViewPager viewPager;
    private SamplePagerAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_images);
        initState();
        initToobar();
        initView();
    }

    private void initState(){
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        mList = intent.getStringArrayListExtra("arrayList");
//        Log.e("123","pos="+position+" list="+mList);
        isShown = true;
        isScale = true;
    }

    private void initToobar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView(){
        mShare = (ImageButton)findViewById(R.id.ib_share);
        mDelete = (ImageButton)findViewById(R.id.ib_delete);
        mScale = (ImageButton)findViewById(R.id.ib_scale);
        bottom = (FrameLayout)findViewById(R.id.fl_bottom);
        mShare.setOnClickListener(this);
        mScale.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        mImagePosition = (TextView)findViewById(R.id.tv_bigimage_position);
        mImagePosition.setText((position+1) + "/" + mList.size());
        viewPager = (HackyViewPager) findViewById(R.id.viewPager);
        try{
            adapter = new SamplePagerAdapter();
        }catch(OutOfMemoryError e){
            System.out.println("OutOfMemory");
        }
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.setOnPageChangeListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_scale:
                if(!isScale){
                    isScale = true;
                    mScale.setImageResource(R.mipmap.icon_scale_16_9);
                }else{
                    isScale = false;
                    mScale.setImageResource(R.mipmap.icon_scale_4_3);
                }
                adapter.setScale(isScale);
                adapter.notifyDataSetChanged();
                break;
            case R.id.ib_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse("file://"+mList.get(position));
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_pic)));

                break;
            case R.id.ib_delete:
                Dialog alertDialog = new AlertDialog.Builder(BigImagesActivity.this).
                        setTitle(getResources().getString(R.string.big_image_activity_dialog_title_remove)).
                        setMessage(getResources().getString(R.string.big_image_activity_dialog_message)).
                        setIcon(R.drawable.expander_ic_minimized).
                        setPositiveButton(getResources().getString(R.string.big_image_activity_dialog_yes_button_name), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                FileUtils.deleteImage(new File(mList.get(position)));
                                mList.remove(position);
                                mImagePosition.setText((position+1) + "/" + mList.size());
                                adapter.notifyDataSetChanged();
                            }
                        }).
                        setNegativeButton(getResources().getString(R.string.big_image_activity_dialog_no_button_name), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub

                            }
                        }).
                        create();
                alertDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        if(isShown){
            mToolbar.setVisibility(View.GONE);
            bottom.setVisibility(View.GONE);
            isShown = false;
        }else{
            mToolbar.setVisibility(View.VISIBLE);
            bottom.setVisibility(View.VISIBLE);
            isShown = true;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        mImagePosition.setText((position+1) + "/" + mList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {


    }

    class SamplePagerAdapter extends PagerAdapter {
        private boolean scale;

        public SamplePagerAdapter() {
            super();
            scale = true;
        }
        public boolean isScale() {
            return scale;
        }

        public void setScale(boolean scale) {
            this.scale = scale;
        }
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            System.out.println("instatiateItem position:"+position);
            //获取手机屏幕宽度
            int requiredWidthSize = PhoneConfig.getPhoneWidth(BigImagesActivity.this);
            PhotoView photoView = new PhotoView(container.getContext());
            Date d = new Date(new File(mList.get(position)).lastModified());
            SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("最后修改时间："+foo.format(d));
            System.out.println("最后修改时间："+new File(mList.get(position)).lastModified());
//            Log.i("123", "view:"+position);
            try {
                if (scale) {
//                Log.e("123", "scale  view:"+position+"file name:"+mList.get(position));
//				Bitmap bm = ScaleImageUtils.resizeImage(ScaleImageUtils.decodeFile(requiredWidthSize,requiredWidthSize * 9 / 16
//						,new File(mList.get(1))),requiredWidthSize , requiredWidthSize * 9 / 16);

                    photoView.setImageBitmap(/*sDrawables[position]*/ScaleImageUtils.resizeImage(ScaleImageUtils.decodeFile(requiredWidthSize, requiredWidthSize * 9 / 16
                            , new File(mList.get(position))), requiredWidthSize, requiredWidthSize * 9 / 16));


//				im.setImageBitmap(bm);
                } else {
//                Log.e("123", "no scale  view:"+position);
                    photoView.setImageBitmap(ScaleImageUtils.decodeFile(requiredWidthSize, requiredWidthSize * 3 / 4
                            , new File(mList.get(position))));
                }
            }catch (Exception e){
                photoView.setImageResource(R.mipmap.local_file_bg2);
                return photoView;
            }
            //注册点击事件
            photoView.setOnViewTapListener(BigImagesActivity.this);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setTag(position);
            return photoView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


}
