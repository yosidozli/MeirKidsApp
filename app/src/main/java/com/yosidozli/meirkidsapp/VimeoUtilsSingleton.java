package com.yosidozli.meirkidsapp;


import android.content.Context;

import com.vimeo.networking.Configuration;
import com.vimeo.networking.Vimeo;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.VideoFile;
import com.vimeo.networking.model.error.VimeoError;

import java.util.ArrayList;

import okhttp3.CacheControl;

/**
 * Created by yosid on 01/11/2017.
 */

class VimeoUtilsSingleton {

    private static VimeoUtilsSingleton instance;
    private Context mContext;
    private VimeoClient mApiClient;
    private ModelCallback<Video> modelCallback;

    public static VimeoUtilsSingleton getInstance( Context context){
        if(instance == null) {
            instance = new VimeoUtilsSingleton(context);
            instance.initialize();
        }
        return instance;
    }


    private VimeoUtilsSingleton(Context context){
        mContext = context;
        instance = this;
    }

    private VimeoUtilsSingleton(){
    }

    public Configuration.Builder getAccessTokenBuilder(){
        //String accessToken = "e9a39063cb6bc99f012f97612918114b";
        String accessToken = "e22d6459a925782c77cab70224fa5d45";
        return new Configuration.Builder(accessToken);
    }

    public void initialize(){
       Configuration.Builder builder = getAccessTokenBuilder();
       builder.enableCertPinning(false);
       builder.setLogLevel(Vimeo.LogLevel.ERROR);
       VimeoClient.initialize(builder.build());
       mApiClient = VimeoClient.getInstance();
    }

    public void fetchLesson(final int requestId , ModelCallback<Video> modelCallback){
        String uri ="https://api.vimeo.com/videos/" + requestId;
        mApiClient.fetchContent(uri, CacheControl.FORCE_NETWORK,modelCallback);
    }

    public void fetchLesson(Lesson lesson , ModelCallback<Video> modelCallback){
        fetchLesson(Integer.parseInt( lesson.getVimeoId()), modelCallback);
    }


    public String getVideoLink(Lesson lesson) {
        final ArrayList<VideoFile> videoFiles = new ArrayList<>();
        ModelCallback<Video> callback = new ModelCallback<Video>(Video.class) {
            @Override
            public void success(Video video) {
                videoFiles.addAll(video.getDownload());
                synchronized (this) {
                    notifyAll();
                }
            }

            @Override
            public void failure(VimeoError error) {
                synchronized (this) {
                    notifyAll();
                }
            }
        };
        fetchLesson(lesson, callback);
        synchronized (callback) {
            try {
                callback.wait(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return videoFiles.get(1).getLink();
        }
    }

    public static interface Listener{
        public void downloadStarted();
        public void finishedDownloading(boolean fromCache);
    }
}
