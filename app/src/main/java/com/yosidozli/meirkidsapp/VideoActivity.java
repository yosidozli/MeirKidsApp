package com.yosidozli.meirkidsapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.yosidozli.meirkidsapp.registration.User;

import java.io.InputStream;
import java.sql.Connection;
import java.util.List;

public class VideoActivity extends AppCompatActivity implements LessonAdapter.ListItemClickListener {
    String mediaUri = "http://media3.meirkids.co.il";///131/059/6//Idx_5968807.mp4"; //"rtmp://192.168.77.2:1935//michael/_definst_mp4:MeirKidsNew/131/059/6/Idx_5968807.mp4";
    String notApprovedMediaUri = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    SimpleExoPlayerView mPlayerView;
    SimpleExoPlayer mPlayer;
    Lesson mLesson;
    private LessonAdapter mLessonAdapter;
    private RecyclerView mLessonsList;
    long position=0;
    private final static String POSITION = "position";
    private final static String TAG = "VideoActivity";
    private static int layout = R.layout.new_item_content_layout_video;
    private User mUser;
    private PreferencesUtils prefUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenConfigurations(getResources().getConfiguration());
        forceRTLIfSupported();
        prefUtils = new PreferencesUtils(this);
        mUser =prefUtils.getUserFromPreferences();

        //Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_video);
        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoPlayerView);
        mLesson = (Lesson) getIntent().getSerializableExtra("Lesson");

        mLessonsList = (RecyclerView) findViewById(R.id.lesson_rv);

            mLessonsList.setLayoutManager( new LinearLayoutManager(VideoActivity.this));
            mLessonsList.setHasFixedSize(true);
            mLessonAdapter = new LessonAdapter(MainActivity.staticLesson,VideoActivity.this,layout);
            if(mUser == null || !mUser.isApproved())
                mLessonAdapter.setShouldShowRegister(true);
            mLessonsList.setAdapter(mLessonAdapter);





    }

    private void setScreenConfigurations(Configuration config){
        int uiOptions = 0;
        if (Build.VERSION.SDK_INT >=19) {
            uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }else{
            uiOptions =View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        if(mLessonsList !=null && config.orientation == Configuration.ORIENTATION_LANDSCAPE){
            //Log.d(TAG, "setScreenConfiguration: Orientation Landscape");
            mLessonsList.setVisibility(View.GONE);


        }else if(mLessonsList !=null && config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Log.d(TAG, "setScreenConfiguration: Orientation Portrait");

            mLessonsList.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onDestroy() {
        //Log.d(TAG,"onDestroy");
        releasePlayer();
        super.onDestroy();

    }
    @Override
    protected void  onStop(){
        //Log.d(TAG,"onStop");
        super.onStop();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        //Log.d(TAG,"onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        long pos = savedInstanceState.getLong(POSITION,0);
        if(mPlayer != null)
            mPlayer.seekTo(pos);
       // super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    protected void onResume(){
        super.onResume();
        //Log.d(TAG,"onResume");
        setScreenConfigurations(getResources().getConfiguration());
        initializePlayer(getUriToPlay());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setScreenConfigurations(newConfig);
    }

    private Uri getUriToPlay(){
        //Log.d(TAG, "getUriToPlay: "+mLesson.isForUsersOnly());
        if(mLesson.isForUsersOnly() ){
            //Log.d(TAG, "getUriToPlay: check if mUser is null "+(mUser == null));
            if(mUser ==null || !mUser.isApproved())
             return Uri.parse(mediaUri+mLesson.getCropUrl());
        }
        Log.d(TAG, "getUriToPlay: "+mediaUri);
        Log.d(TAG, "getUriToPlay: "+mLesson.getPostUrl());
        return Uri.parse(mediaUri+mLesson.getPostUrl());

    }


    private void initializePlayer(final Uri mediaUri){
        if(mPlayer == null){
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector,loadControl);
            mPlayerView.setPlayer(mPlayer);
            mPlayer.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.d(TAG, "onPlayerStateChanged: "+mediaUri);

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    Log.e(TAG, "onPlayerError: "+mediaUri,error );

                }

                @Override
                public void onPositionDiscontinuity() {

                }
            });
            String userAgent = Util.getUserAgent(this,"MeirKidsApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                    new DefaultDataSourceFactory(this,userAgent),
                    new DefaultExtractorsFactory(),null,null);
            mPlayer.prepare(mediaSource);
            mPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer(){
        if(mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Log.d(TAG,"onSavedInstanceState");
        position = mPlayer.getCurrentPosition();
        outState.putLong(POSITION,position);
        mPlayer.setPlayWhenReady(false);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if((mUser != null && mUser.isApproved()) || clickedItemIndex != 0) {
            releasePlayer();
            mLesson = MainActivity.staticLesson.get(clickedItemIndex);
            initializePlayer(getUriToPlay());
        }else{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.url_registration)));
            startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }
}
