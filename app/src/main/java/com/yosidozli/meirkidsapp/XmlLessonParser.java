package com.yosidozli.meirkidsapp;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yosid on 18/05/2017.
 */

public class XmlLessonParser {
    private static final String ns = null;
    private static final String ARRAY_OF_LESSONS= "ArrayOfLessonToMeirtv";
    private static final String LESSON= "LessonToMeirtv";
    private static final String TITLE= "Title";
    private static final String VIDEO_URL= "VideoPath";
    private static final String IMAGE_PATH= "ImagePath";
    private static final String SET_NAME= "SetName";
    private static final String USERS_ONLY= "ForUsersOnly";
    private static final String CROP_URL="VideoCrop";
    private static final String SET_ID="LessonSet_ID";



    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readLessonArray(parser);
        } finally {
            in.close();
        }
    }



    private List readLessonArray (XmlPullParser parser) throws XmlPullParserException, IOException {

    List lessons = new ArrayList();

        parser.require(XmlPullParser.START_TAG,ns,ARRAY_OF_LESSONS);

        while (parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String name = parser.getName();

            if(name.equals(LESSON)) {

                lessons.add(readLesson(parser));

            }else {
                skip(parser);
            }



        }
        return lessons;

    }




    private Lesson readLesson(XmlPullParser parser) throws XmlPullParserException,IOException{
        parser.require(XmlPullParser.START_TAG,ns,LESSON );

        String title = null;
        String imageUrl = null;
        String setName = null;
        String postUrl = null;
        String cropUrl = null;
        String lessonSetID = null;
        String usersOnly = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;


            String name = parser.getName();
            if (name.equals(TITLE)) {
                title = readTitle(parser);
            } else if (name.equals(VIDEO_URL)) {
                postUrl = readPostUrl(parser);
            } else if (name.equals(CROP_URL)) {
                cropUrl = readCropUrl(parser);
            } else if (name.equals(IMAGE_PATH)) {
                imageUrl = readImageUrl(parser);
            } else if (name.equals(SET_NAME)) {
                setName = readSetName(parser);
            }else if (name.equals(USERS_ONLY)) {
                 usersOnly  = readUsersOnly(parser);
            }else if (name.equals(SET_ID)) {
                lessonSetID  = readLessonSetID(parser);
            } else {
                skip(parser);
            }

        }

        return new Lesson(title,imageUrl, setName,postUrl,cropUrl, lessonSetID, Boolean.valueOf(usersOnly));
    }

    private String readUsersOnly(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG,ns,USERS_ONLY);
        String usersOnly = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,USERS_ONLY);
        return usersOnly;
    }

    private String readTitle(XmlPullParser parser) throws  IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,TITLE);
        return title;

    }

    private String readPostUrl(XmlPullParser parser) throws  IOException,XmlPullParserException{

        parser.require(XmlPullParser.START_TAG,ns, VIDEO_URL);
        String postUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, VIDEO_URL);
        return postUrl;
    }

    private String readCropUrl(XmlPullParser parser) throws  IOException,XmlPullParserException{

        parser.require(XmlPullParser.START_TAG,ns, CROP_URL);
        String cropUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, CROP_URL);
        return cropUrl;
    }


    private String readImageUrl(XmlPullParser parser) throws  IOException,XmlPullParserException{

        parser.require(XmlPullParser.START_TAG,ns, IMAGE_PATH);
        String imageUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, IMAGE_PATH);
        return imageUrl;
    }

    private String readSetName(XmlPullParser parser) throws  IOException,XmlPullParserException{

        parser.require(XmlPullParser.START_TAG,ns, SET_NAME);
        String setName = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, SET_NAME);
        return setName;
    }

    private String readLessonSetID(XmlPullParser parser) throws  IOException,XmlPullParserException{

        parser.require(XmlPullParser.START_TAG,ns, SET_ID);
        String setName = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, SET_ID);
        return setName;
    }


    private String readTextElement(XmlPullParser parser, String name ) throws  IOException,XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns, name);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, name);
        return text;

    }

    private String readText(XmlPullParser parser) throws IOException,XmlPullParserException{
        String result = "";
        if(parser.next() == XmlPullParser.TEXT){
            result = parser.getText();
            parser.nextTag();
        }
        return result;

    }

    private void skip(XmlPullParser parser) throws  XmlPullParserException,IOException{
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while(depth != 0){
            switch (parser.next()){
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }

        }

    }






}


