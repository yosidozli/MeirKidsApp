package com.yosidozli.meirkidsapp;

import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.error.VimeoError;

/**
 * Created by yosid on 10/11/2017.
 */

public class VimeoLesson extends Lesson {
    private Lesson lesson;
    private static boolean TESTING = false;


    private String vimeoLink = null;

    public String getVimeoLink() {
        return vimeoLink;
    }

    public void setVimeoLink(String vimeoLink) {
        this.vimeoLink = vimeoLink;
    }



    private ModelCallback<Video> callback;

    public VimeoLesson(Lesson lesson ) {
        super(lesson.getTitle(), lesson.getImageUrl(),lesson.getSetName(),lesson.getPostUrl(), lesson.getCropUrl(), lesson.getLessonSetID(), lesson.isForUsersOnly() ,lesson.getVimeoId());


        this.lesson = lesson;



    }

    @Override
    public String getPostUrl(){




        String result = "";

        if(vimeoLink != null)
            result = vimeoLink;
        else
            result = lesson.getPostUrl();

        return result;
    }

    public static void fetchFromVimeo(VimeoLesson vimeoLesson,VimeoUtilsSingleton utilsSingleton, VimeoUtilsSingleton.Listener listener, ModelCallback<Video> modelCallback){

        utilsSingleton.fetchLesson(vimeoLesson,modelCallback);
        listener.downloadStarted();
    }

    public static void fetchFormVimeo(final VimeoLesson vimeoLesson, VimeoUtilsSingleton utilsSingleton,final VimeoUtilsSingleton.Listener listener){
        ModelCallback<Video> modelCallback = new ModelCallback<Video>(Video.class){

            @Override
            public void success(Video video) {
                vimeoLesson.setVimeoLink(video.getDownload().get(1).getLink());
                listener.finishedDownloading();
            }

            @Override
            public void failure(VimeoError error) {
                listener.finishedDownloading();
            }
        };
        if(vimeoLesson.getVimeoLink() == null)
            fetchFromVimeo(vimeoLesson,utilsSingleton,listener,modelCallback);
        else
            listener.finishedDownloading();

    }








}
