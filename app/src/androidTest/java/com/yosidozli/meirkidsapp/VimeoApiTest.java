//package com.yosidozli.meirkidsapp;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import com.vimeo.networking.Configuration;
//import com.vimeo.networking.VimeoClient;
//import com.vimeo.networking.callbacks.ModelCallback;
//import com.vimeo.networking.model.Video;
//import com.vimeo.networking.model.VideoFile;
//import com.vimeo.networking.model.error.VimeoError;
//
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//
//import java.util.ArrayList;
//
//import static junit.framework.Assert.assertEquals;
//import static junit.framework.Assert.assertNotNull;
//import static junit.framework.Assert.assertTrue;
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.when;
//
//
///**
// * Created by yosid on 01/11/2017.
// */
//
//
//@RunWith(AndroidJUnit4.class)
//public class VimeoApiTest {
//
//    private Context context;
//    private VimeoUtilsSingleton utilsSingleton;
//    public static final String TAG = "VimeoApiTest";
//
//
//    @Before
//    public void setUp(){
//     context =    InstrumentationRegistry.getTargetContext().getApplicationContext();
//     utilsSingleton =  VimeoUtilsSingleton.getInstance(context);
//     utilsSingleton.initialize();
//    }
//
//    @Test
//    public void vimeoUtilsSingletonGetInstanceTest(){
//
//        VimeoUtilsSingleton secondSingleton =  VimeoUtilsSingleton.getInstance(context);
//        assertEquals(utilsSingleton,secondSingleton);
//
//    }
//
//    @Test
//    public void testGetAccessTokenBuilder(){
//        Configuration.Builder builder = utilsSingleton.getAccessTokenBuilder();
//        assertEquals(builder.getClass(),Configuration.Builder.class);
//    }
//
//    @Test
//    public void testInitialization(){
//        assertNotNull( VimeoClient.getInstance());
//    }
//
//    @Test
//    public void testFetchLesson(){
//        String id = "Id_5969083_29";
//        final int requestId  = 240794471;
//        final StringBuilder resultId = new StringBuilder();
//        ModelCallback<Video> callback = new ModelCallback<Video>(Video.class){
//
//            @Override
//            public void success(Video video) {
//                resultId.append(video.name);
//                synchronized (this){
//                    notifyAll();
//                }
//            }
//
//            @Override
//            public void failure(VimeoError error) {
//                resultId.append(error.getLogString());
//            }
//        };
//
//        utilsSingleton.fetchLesson(requestId ,callback);
//        synchronized (callback){
//            try {
//                callback.wait(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        assertEquals(id,resultId.toString());
//
//    }
//
//
//    @Test
//    public void testFetchVideoFile() {
//        final int requestId1  = 240959476;
//        final int requestId2 = 239809697;
//        final int requestId3 = 239307434;
//
//        final ArrayList<VideoFile> videoFiles = new ArrayList<>();
//        ModelCallback<Video> callback = new ModelCallback<Video>(Video.class){
//            @Override
//            public void success(Video video) {
//                videoFiles.addAll(video.getDownload());
//                synchronized (this){
//                    notifyAll();
//                }
//            }
//            @Override
//            public void failure(VimeoError error) {
//            }
//        };
//        utilsSingleton.fetchLesson(requestId1 ,callback);
//        synchronized (callback){
//            try {
//                callback.wait(30000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        String link = videoFiles.get(1).getLink();
//        assertTrue(link.contains("https://player.vimeo"));
//    }
//
//    @Test
//    public void testFetchVideoFilesFromLesson() {
//        final int requestId1 = 240959476;
//        Lesson lesson = Mockito.mock(Lesson.class);
//        final ArrayList<VideoFile> videoFiles = new ArrayList<>();
//        when(lesson.getVimeoId()).thenReturn(String.valueOf( requestId1));
//
//        ModelCallback<Video> callback = new ModelCallback<Video>(Video.class) {
//            @Override
//            public void success(Video video) {
//
//                videoFiles.addAll(video.getDownload());
//                synchronized (this) {
//                    notifyAll();
//                }
//            }
//
//            @Override
//            public void failure(VimeoError error) {
//                synchronized (this) {
//                    notifyAll();
//                }
//            }
//        };
//        utilsSingleton.fetchLesson(lesson, callback);
//        synchronized (callback) {
//            try {
//                callback.wait(30000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            for (VideoFile v: videoFiles){
//                Log.d(TAG, "testFetchVideoFilesFromLesson: "+v.getQuality());
//                Log.d(TAG, "testFetchVideoFilesFromLesson: "+v.getSize());
//                Log.d(TAG, "testFetchVideoFilesFromLesson: "+v.getType());
//                Log.d(TAG, "testFetchVideoFilesFromLesson: "+v.getWidth());
//
//            }
//            assertEquals(5, videoFiles.size());
//
//        }
//    }
//
//    @Test
//    public void testGetLessonLink(){
//        Lesson lesson = Mockito.mock(Lesson.class);
//        when(lesson.getVimeoId()).thenReturn(String.valueOf( 240959476));
//        assertTrue(utilsSingleton.getVideoLink(lesson).contains("https://player.vimeo"));
//
//    }
//    @Ignore
//    @Test
//    public void testGetVimeoLinkWithStandadClient(){
//        Lesson lesson = Mockito.mock(Lesson.class ,Mockito.RETURNS_DEEP_STUBS);
//        when(lesson.getVimeoId()).thenReturn((String.valueOf(240959476)));
//        VimeoLesson vimeoLesson = new VimeoLesson(lesson);
//        assertTrue(vimeoLesson.getPostUrl().contains("https://player.vimeo"));
//    }
//
//    @Test
//    public void testVimeoLessonCreation(){
//        Lesson lesson = Mockito.mock(Lesson.class, Mockito.RETURNS_DEEP_STUBS);
//        when(lesson.getVimeoId()).thenReturn((String.valueOf(240959476)));
//
//        VimeoLesson vimeoLesson = new VimeoLesson(lesson );
//        assertNotNull(vimeoLesson);
//
//
//    }
//
//    @Test
//    public void testNoVimeoIdReturnPostUrl(){
//        Lesson lesson = Mockito.mock(Lesson.class ,Mockito.RETURNS_DEEP_STUBS);
//        when(lesson.getVimeoId()).thenReturn((String.valueOf(2476)));
//        doReturn("postUrl").when(lesson).getPostUrl();
//      //  lesson.setPostUrl("postUrl");
//        VimeoLesson vimeoLesson = new VimeoLesson(lesson);
//        assertEquals("postUrl" , vimeoLesson.getPostUrl());
//
//    }
//
//    @Test
//    public void testFetchVideoLinkAndUpdateVimeoLesson(){
//        Lesson lesson = Mockito.mock(Lesson.class, Mockito.RETURNS_DEEP_STUBS);
//        when(lesson.getVimeoId()).thenReturn((String.valueOf(240959476)));
//        final VimeoLesson vimeoLesson = new VimeoLesson(lesson );
//        final VimeoUtilsSingleton.Listener listener = new VimeoUtilsSingleton.Listener() {
//            @Override
//            public void downloadStarted() {
//                synchronized (this){
//                    try {
//                        this.wait(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void finishedDownloading() {
//                synchronized (this) {
//                    this.notifyAll();
//                }
//
//            }
//        };
//        VimeoLesson.fetchFromVimeo(vimeoLesson,utilsSingleton, listener,new ModelCallback<Video>(Video.class){
//
//            @Override
//            public void success(Video video) {
//                vimeoLesson.setVimeoLink(video.getDownload().get(1).getLink());
//                listener.finishedDownloading();
//            }
//
//            @Override
//            public void failure(VimeoError error) {
//                listener.finishedDownloading();
//            }
//        });
//
//        assertTrue( vimeoLesson.getPostUrl().contains("https://player.vimeo"));
//
//    }
//
//
//    @Test
//    public void testFetchVideoLinkAndUpdateVimeoLessonNoCallback(){
//        Lesson lesson = Mockito.mock(Lesson.class, Mockito.RETURNS_DEEP_STUBS);
//        when(lesson.getVimeoId()).thenReturn((String.valueOf(240959476)));
//        final VimeoLesson vimeoLesson = new VimeoLesson(lesson );
//        final VimeoUtilsSingleton.Listener listener = new VimeoUtilsSingleton.Listener() {
//            @Override
//            public void downloadStarted() {
//                synchronized (this){
//                    try {
//                        this.wait(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void finishedDownloading() {
//                synchronized (this) {
//                    this.notifyAll();
//                }
//
//            }
//        };
//        VimeoLesson.fetchFormVimeo(vimeoLesson,utilsSingleton,listener);
//
//        assertTrue( vimeoLesson.getPostUrl().contains("https://player.vimeo"));
//
//    }
//
//    @Test
//    public void testFetchVideoLinkAndUpdateVimeoLessonVimeoLinkNotNull(){
//        Lesson lesson = Mockito.mock(Lesson.class, Mockito.RETURNS_DEEP_STUBS);
//        when(lesson.getVimeoId()).thenReturn((String.valueOf(240959476)));
//        final VimeoLesson vimeoLesson = new VimeoLesson(lesson );
//        vimeoLesson.setVimeoLink("vimeo link allready exists");
//        final VimeoUtilsSingleton.Listener listener = new VimeoUtilsSingleton.Listener() {
//            @Override
//            public void downloadStarted() {
//                synchronized (this){
//                    try {
//                        this.wait(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void finishedDownloading() {
//                synchronized (this) {
//                    this.notifyAll();
//                }
//
//            }
//        };
//        VimeoLesson.fetchFormVimeo(vimeoLesson,utilsSingleton,listener);
//
//        assertEquals("vimeo link allready exists",vimeoLesson.getPostUrl());
//
//    }
//
//
//
//
//
//}
