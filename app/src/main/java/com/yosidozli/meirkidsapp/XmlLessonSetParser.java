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

public class XmlLessonSetParser {
    private static final String ns = null;
    private static final String ARRAY_OF_SETS = "ArrayOfSetToMeirtv";
    private static final String SET = "SetToMeirtv";
    private static final String TITLE= "Title";
    private static final String BIG_IMAGE_PATH= "BigImagePath";
    private static final String SMALL_IMAGE_PATH= "SmallImagePath";
    private static final String ID = "Idx";




    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readSetArray(parser);
        } finally {
            in.close();
        }
    }

    private List readSetArray(XmlPullParser parser) throws XmlPullParserException, IOException {

    List lessons = new ArrayList();

        parser.require(XmlPullParser.START_TAG,ns, ARRAY_OF_SETS);

        while (parser.next() != XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String name = parser.getName();

            if(name.equals(SET)) {

                lessons.add(readSet(parser));

            }else {
                skip(parser);
            }



        }
        return lessons;

    }


    private LessonSet readSet(XmlPullParser parser) throws XmlPullParserException,IOException{
        parser.require(XmlPullParser.START_TAG,ns, SET);

        String id =null;
        String title = null;
        String bigImagePath = null;
        String smallImagePath = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;


            String name = parser.getName();
            if (name.equals(TITLE)) {
                title = readTitle(parser);
            } else if (name.equals(ID)) {
                id = readId(parser);
            } else if (name.equals(BIG_IMAGE_PATH)) {
                bigImagePath = readBigImagePath(parser);
            } else if (name.equals(SMALL_IMAGE_PATH)) {
                smallImagePath = readSmallImagePath(parser);

            } else {
                skip(parser);
            }

        }

        return new LessonSet(id,title, bigImagePath,smallImagePath);
    }



    private String readTitle(XmlPullParser parser) throws  IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,TITLE);
        return title;

    }

    private String readId(XmlPullParser parser) throws  IOException,XmlPullParserException{

        parser.require(XmlPullParser.START_TAG,ns, ID);
        String postUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, ID);
        return postUrl;
    }


    private String readBigImagePath(XmlPullParser parser) throws  IOException,XmlPullParserException{

        parser.require(XmlPullParser.START_TAG,ns, BIG_IMAGE_PATH);
        String imageUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, BIG_IMAGE_PATH);
        return imageUrl;
    }

    private String readSmallImagePath(XmlPullParser parser) throws  IOException,XmlPullParserException{

        parser.require(XmlPullParser.START_TAG,ns, SMALL_IMAGE_PATH);
        String setName = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, SMALL_IMAGE_PATH);
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


