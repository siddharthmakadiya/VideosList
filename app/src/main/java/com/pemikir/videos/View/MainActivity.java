package com.pemikir.videos.View;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.pemikir.videos.Fragment.ExternalVideoListFragment;
import com.pemikir.videos.Fragment.AllVideoListFragment;
import com.pemikir.videos.Fragment.InternalVideoListFragment;
import com.pemikir.videos.Model.SharedPreference;
import com.pemikir.videos.R;
import com.pemikir.videos.VideoUtils.Utils;

import java.util.ArrayList;
import java.util.List;

import me.everything.providers.android.media.MediaProvider;
import me.everything.providers.android.media.Video;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    CoordinatorLayout back_layout;

    InterstitialAd adverd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

//        AdView mAdView = (AdView) findViewById(R.id.adView);

        if (Utils.isConnectingToInternet(getApplicationContext())) {
            adverd = Utils.newInterstitialAd(getApplicationContext());
            AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
            adverd.loadAd(adRequest);

            if (adverd.isLoaded()) {
                adverd.show();
            }

//            mAdView.loadAd(new AdRequest.Builder().build());
        }


        back_layout = (CoordinatorLayout) findViewById(R.id.background);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setUnderlineColor(Color.TRANSPARENT);
        tabs.setTextColor(Color.WHITE);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setIndicatorHeight(3);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setViewPager(viewPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            Log.i("Shared", "" + new SharedPreference().getWallpaperurl(MainActivity.this));
            if (!TextUtils.isEmpty(new SharedPreference().getWallpaperurl(MainActivity.this))) {
                back_layout.setBackground(new BitmapDrawable(new SharedPreference().getWallpaperurl(MainActivity.this)));
            }
        }

//        try {
//            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.container, new ExternalVideoListFragment());
//            fragmentTransaction.commit();
//        } catch (Exception e) {
//        }
//
//        printNamesToLogCat(MainActivity.this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExternalVideoListFragment(), "EXTERNAL");
        adapter.addFragment(new AllVideoListFragment(), "ALL");
        adapter.addFragment(new InternalVideoListFragment(), "INTERNAL");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem settingmenu = menu.findItem(R.id.action_settings);

        settingmenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentGallery.setType("image/*");
                intentGallery.putExtra("return-data", true);
                startActivityForResult(intentGallery, 1001);

                Toast.makeText(getApplicationContext(), "Setting Activity", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode != Activity.RESULT_CANCELED) {

            if (requestCode == 1001) {
                // filePath = getRealPathFromURI(selectedImage);
                Uri selectedImage = data.getData();
                String[] filePathColumn =
                        {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    back_layout.setBackground(new BitmapDrawable(filePath));
                    new SharedPreference().saveWallpaperurl(MainActivity.this, filePath);
                }

                cursor.close();

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adverd.isLoaded()) {
            adverd.show();
        }
    }
}
