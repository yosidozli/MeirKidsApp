package Utils;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by yosid on 17/11/2017.
 */
public class MyLoggerTest {
    MyLogger logger;
    int year;
    int month;
    int day;
    int token;



    @Before
    public void setUp() throws Exception {
        logger = new MyLogger();
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DATE);
        token = year+month+day;
    }

    @Test
    public void getToken() throws Exception {


        assertEquals("Token is not as expected" +
                "",token,logger.getToken());
    }

    @Test
    public void logUserLogin() throws Exception {
        String loginUrl = "http://crm.meirkids.co.il/api/app_login.asp?CrmID=2953&Token="+token;
        logger.logUserLogin(String.valueOf(2953));
        assertEquals("url not as expected",loginUrl, logger.getUrl());

    }

    @Test
    public void logLessonChosen() throws Exception {
        String watchingUrl ="http://crm.meirkids.co.il/api/app_show.asp?LessonID=5969102&CrmID=2953&Token="+token;
        logger.logLessonChosen(String.valueOf(2953), String.valueOf(5969102));
        assertEquals("url not as expected",watchingUrl, logger.getUrl());

    }

}

