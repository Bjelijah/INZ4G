package com.inz.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.inz.action.LoginAction;
import com.inz.activity.fragment.DeviceFragment;
import com.inz.activity.fragment.HomeBaseFragment;
import com.inz.activity.fragment.MediaFragment;
import com.inz.inz4g.R;
import com.inz.utils.SDCardUtils;
import com.inz.utils.UserConfigSp;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/27.
 */

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private ViewPager mViewPager;
    private FloatingActionButton mAddbtn;
    private List<HomeBaseFragment> mFragments;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        //loadBackdrop();//fixme  we dont need backdrop just black background
        initFragment();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
//        toolbar.inflateMenu(R.menu.center_setting_action_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(LoginAction.getInstance().getmInfo().getAccount());
        mAddbtn = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mAddbtn.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_outdent).actionBar().color(Color.WHITE));
        mAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo back
                funExit();
            }
        });
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        if (UserConfigSp.loadLike(this)){
            Bitmap bitmap = loadLikeBk();
            imageView.setImageBitmap(bitmap);
        }else {
            Glide.with(this).load("https://unsplash.it/600/300/?random").centerCrop().into(imageView);
        }
    }

    private void initFragment(){
        mFragments = new ArrayList<>();
        mFragments.add(new DeviceFragment());
        mFragments.add(new MediaFragment());
        MyFragmentPagerAdatper myFragmentPagerAdatper = new MyFragmentPagerAdatper(getSupportFragmentManager(),mFragments);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(myFragmentPagerAdatper);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        switch (mViewPager.getCurrentItem()){
            case 0:
                menu.findItem(R.id.menu_home_help).setVisible(false);
                menu.findItem(R.id.menu_home_like).setVisible(false);
                if(UserConfigSp.loadLike(this)) {menu.findItem(R.id.menu_home_like).setIcon(getDrawable(R.mipmap.ic_favorite_white_24dp));}else{
                    menu.findItem(R.id.menu_home_like).setIcon(getDrawable(R.mipmap.ic_favorite_border_white_24dp));}
                menu.findItem(R.id.menu_home_notice_search).setVisible(false);
                menu.findItem(R.id.menu_home_scope).setVisible(false);
                menu.findItem(R.id.menu_home_setting).setVisible(false);
                menu.findItem(R.id.menu_home_notice_unread).setVisible(false);
                menu.findItem(R.id.menu_home_notice_read).setVisible(false);
                menu.findItem(R.id.menu_home_notice_all).setVisible(false);
                break;

            case 1:
                menu.findItem(R.id.menu_home_like).setVisible(false);
                menu.findItem(R.id.menu_home_help).setVisible(false);
                menu.findItem(R.id.menu_home_notice_search).setVisible(false);
                menu.findItem(R.id.menu_home_scope).setVisible(false);
                menu.findItem(R.id.menu_home_setting).setVisible(false);
                menu.findItem(R.id.menu_home_notice_unread).setVisible(false);
                menu.findItem(R.id.menu_home_notice_read).setVisible(false);
                menu.findItem(R.id.menu_home_notice_all).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.menu_home_like).setVisible(false);
                menu.findItem(R.id.menu_home_help).setVisible(false);
                menu.findItem(R.id.menu_home_notice_search).setVisible(true);
                menu.findItem(R.id.menu_home_scope).setVisible(false);
                menu.findItem(R.id.menu_home_setting).setVisible(false);
                menu.findItem(R.id.menu_home_notice_unread).setVisible(true);
                menu.findItem(R.id.menu_home_notice_read).setVisible(true);
                menu.findItem(R.id.menu_home_notice_all).setVisible(true);
                break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home_like:
                //TODO:
                if(UserConfigSp.loadLike(this)) {
                    item.setIcon(getDrawable(R.mipmap.ic_favorite_border_white_24dp));
                    UserConfigSp.saveLike(this,false);
                    removeLikeBk();
                }else{
                    item.setIcon(getDrawable(R.mipmap.ic_favorite_white_24dp));
                    UserConfigSp.saveLike(this,true);
                    saveLikeBk();
                }
                break;
            case R.id.menu_home_help:
                break;
            case R.id.menu_home_notice_search:
                break;
            case R.id.menu_home_scope:
                break;
            case R.id.menu_home_setting:
                //设置界面
                Intent intent = new Intent(this,ServerSetActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_home_notice_unread:
                break;
            case R.id.menu_home_notice_read:
                break;
            case R.id.menu_home_notice_all:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void funExit(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveLikeBk(){
        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();
        SDCardUtils.saveBmp2Cach(this,bitmap,"like.jpeg");
        imageView.setDrawingCacheEnabled(false);
    }

    private void removeLikeBk(){
        SDCardUtils.removeBmpFromCach(this,"like.jpeg");
    }

    private Bitmap loadLikeBk(){
        return BitmapFactory.decodeFile(SDCardUtils.getLikePath(this,"like.jpeg"));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        invalidateOptionsMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }




    class MyFragmentPagerAdatper extends FragmentPagerAdapter {
        List<HomeBaseFragment> mList;
        List<String> strings;
        public MyFragmentPagerAdatper(FragmentManager fm) {
            super(fm);
        }

        public MyFragmentPagerAdatper(FragmentManager fm,List<HomeBaseFragment> list){
            super(fm);
            mList = list;
            strings = new ArrayList<>();
            strings.add(getString(R.string.home_fragment_devices));
            strings.add(getString(R.string.home_fragment_medias));
            strings.add(getString(R.string.home_fragment_notice));
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= strings.size()) {
                return "第" + position + "个";
            }
            return strings.get(position);
        }
    }


}
