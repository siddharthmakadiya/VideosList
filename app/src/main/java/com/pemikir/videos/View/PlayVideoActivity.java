package com.pemikir.videos.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.pemikir.videos.R;

/**
 * Copyright (C) Christian Schabesberger 2015 <chris.schabesberger@mailbox.org>
 * PlayVideoActivity.java is part of NewPipe.
 * <p/>
 * NewPipe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * NewPipe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with NewPipe.  If not, see <http://www.gnu.org/licenses/>.
 */

public class PlayVideoActivity extends AppCompatActivity {

    //// TODO: 11.09.15 add "choose stream" menu 

    public static final String VIDEO_URL = "video_url";
    public static final String STREAM_URL = "stream_url";
    public static final String VIDEO_TITLE = "video_title";
    private static final String TAG = PlayVideoActivity.class.toString();
    private static final String POSITION = "position";

    private static final long HIDING_DELAY = 3000;
    private static final long TAB_HIDING_DELAY = 100;
    private static final String PREF_IS_LANDSCAPE = "is_landscape";
    private static long lastUiShowTime = 0;
    private String videoUrl = "";
    private ActionBar actionBar;
    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;
    private ProgressBar progressBar;
    private View decorView;
    private boolean uiIsHidden = false;
    private boolean isLandscape = true;
    private boolean hasSoftKeys = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_video);
        Intent intent = getIntent();
        videoUrl = intent.getStringExtra(VIDEO_URL);
        isLandscape = checkIfLandscape();
        hasSoftKeys = checkIfhasSoftKeys();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (mediaController == null) {
            mediaController = new MediaController(this);
        }

        videoView = (VideoView) findViewById(R.id.video_view);
        progressBar = (ProgressBar) findViewById(R.id.play_video_progress_bar);
        try {
            videoView.setMediaController(mediaController);
            videoView.setVideoPath(videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                    showUi();
                } else {
                    videoView.pause();
                }
            }
        });


        Button button = (Button) findViewById(R.id.content_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uiIsHidden) {
                    showUi();
                } else {
                    hideUi();
                }
            }
        });
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE && uiIsHidden) {
                    showUi();
                }
            }
        });

        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        prefs = getPreferences(Context.MODE_PRIVATE);
        if (prefs.getBoolean(PREF_IS_LANDSCAPE, false) && !isLandscape) {
            toggleOrientation();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreatePanelMenu(int featured, Menu menu) {
        super.onCreatePanelMenu(featured, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.video_player, menu);

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, videoUrl);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, getString(R.string.shareDialogTitle)));
                break;
            case R.id.menu_item_screen_rotation:
                toggleOrientation();
                break;
            default:
                Log.e(TAG, "Error: MenuItem not known");
                return false;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
            adjustMediaControllMetrics();
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isLandscape = false;
            adjustMediaControllMetrics();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putInt(POSITION, videoView.getCurrentPosition());
        //videoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt(POSITION);
        //videoView.seekTo(position);
    }

    private void showUi() {
        try {
            uiIsHidden = false;
            mediaController.show(100000);
            actionBar.show();
            adjustMediaControllMetrics();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((System.currentTimeMillis() - lastUiShowTime) >= HIDING_DELAY) {
                        hideUi();
                    }
                }
            }, HIDING_DELAY);
            lastUiShowTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideUi() {
        uiIsHidden = true;
        actionBar.hide();
        mediaController.hide();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void adjustMediaControllMetrics() {
        MediaController.LayoutParams mediaControllerLayout
                = new MediaController.LayoutParams(MediaController.LayoutParams.MATCH_PARENT,
                MediaController.LayoutParams.WRAP_CONTENT);

        if (!hasSoftKeys) {
            mediaControllerLayout.setMargins(20, 0, 20, 20);
        } else {
            int width = getNavigationBarWidth();
            int height = getNavigationBarHeight();
            mediaControllerLayout.setMargins(width + 20, 0, width + 20, height + 20);
        }
        mediaController.setLayoutParams(mediaControllerLayout);
    }

    private boolean checkIfhasSoftKeys() {
        if (Build.VERSION.SDK_INT >= 17) {
            return getNavigationBarHeight() != 0 || getNavigationBarWidth() != 0;
        } else {
            return true;
        }
    }

    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= 17) {
            Display d = getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int displayHeight = displayMetrics.heightPixels;
            return (realHeight - displayHeight);
        } else {
            return 50;
        }
    }

    private int getNavigationBarWidth() {
        if (Build.VERSION.SDK_INT >= 17) {
            Display d = getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int realWidth = realDisplayMetrics.widthPixels;
            int displayWidth = displayMetrics.widthPixels;
            return (realWidth - displayWidth);
        } else {
            return 50;
        }
    }

    public boolean checkIfLandscape() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels < displayMetrics.widthPixels;
    }

    private void toggleOrientation() {
        if (isLandscape) {
            isLandscape = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            isLandscape = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_IS_LANDSCAPE, isLandscape);
        editor.commit();
    }
}
