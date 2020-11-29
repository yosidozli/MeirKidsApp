package com.yosidozli.meirkidsapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import Utils.AnalyticsUtils;
import Utils.MyLogger;
import yosidozli.com.utils.PersistentLruCache;

public class VideoActivity extends AppCompatActivity implements LessonAdapter.ListItemClickListener ,VimeoUtilsSingleton.Listener {
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
    private AnalyticsUtils mAnalyticsUtils;
    private ProgressBar mProgressBar;
    private MyLogger logger;

    private PersistentLruCache<Long,String> linksCache;
    public static final String LINKS_CACHE_NAME = "video_activity_cache"+BuildConfig.VERSION_CODE;
    public static final int CACHE_SIZE = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenConfigurations(getResources().getConfiguration());
        forceRTLIfSupported();
        prefUtils = new PreferencesUtils(this);
        mUser =prefUtils.getUserFromPreferences();
        mAnalyticsUtils = new AnalyticsUtils(this);
        logger = new MyLogger();



        linksCache = PersistentLruCache.createCachefromFile(this,LINKS_CACHE_NAME,CACHE_SIZE) ;



        //Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_video);
        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoPlayerView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
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
        linksCache.persist(this,LINKS_CACHE_NAME);
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
        //todo use polimorphizem instead of casting
//        fetchLessonUrl();
        Log.d(TAG, "onResume: "+mLesson.getMp4Url());
        initializePlayer(Uri.parse(mLesson.getMp4Url()));
    }

    private void fetchLessonUrl(){
//        Log.d(TAG, "fetchLessonUrl: search in cache "+" "+mLesson.getId());

        if(linksCache.get(Long.valueOf(mLesson.getId())) != null){
            ((VimeoLesson)mLesson).setVimeoLink(linksCache.get(Long.valueOf(mLesson.getId())));
//            Log.d(TAG, "fetchLessonUrl: get from cache "+" "+mLesson.getPostUrl());
            finishedDownloading(true);

        }
        else {
//            Log.d(TAG, "fetchLessonUrl: not in cache "+" "+mLesson.getId());
            VimeoLesson.fetchFormVimeo((VimeoLesson) mLesson, VimeoUtilsSingleton.getInstance(this), this);
        }

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
                return Uri.parse(mediaUri + mLesson.getCropUrl());

        }
        String url = mLesson.getPostUrl();
//        Log.d(TAG, "getUriToPlay: "+mediaUri);
//        Log.d(TAG, "getUriToPlay: "+url);
        if (mLesson.getPostUrl().contains("vimeo") && false) {
            return Uri.parse( url);
        }

//        return Uri.parse(mediaUri+mLesson.getPostUrl());
        return Uri.parse(mLesson.getMp4Url());

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
//                    Log.d(TAG, "onPlayerStateChanged: "+mediaUri);
                    if(playbackState == ExoPlayer.STATE_BUFFERING )
                        mProgressBar.setVisibility(View.VISIBLE);
                    else
                        mProgressBar.setVisibility(View.GONE);
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

        position = mPlayer != null? mPlayer.getCurrentPosition():0;
        outState.putLong(POSITION,position);
        if(mPlayer != null)
            mPlayer.setPlayWhenReady(false);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if((mUser != null && mUser.isApproved()) || clickedItemIndex != 0) {
            releasePlayer();
            mLesson = MainActivity.staticLesson.get(clickedItemIndex);
            mAnalyticsUtils.logLesson(mLesson);
            if(mUser != null)
                logger.logLessonChosen(String.valueOf(mUser.getPersonId()),mLesson.getId());
            fetchLessonUrl();

           // initializePlayer(getUriToPlay());
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

    @Override
    public void downloadStarted() {
        mProgressBar.setVisibility(View.VISIBLE);


    }

    @Override
    public void finishedDownloading(boolean fromCache) {
        mProgressBar.setVisibility(View.GONE);
        if(!fromCache) {
            linksCache.put(Long.parseLong(mLesson.getId()), mLesson.getPostUrl());
//            Log.d(TAG, "finishedDownloading: put in cache "+mLesson.getId()+" "+mLesson.getPostUrl());
        }
        initializePlayer(getUriToPlay());
    }
}
