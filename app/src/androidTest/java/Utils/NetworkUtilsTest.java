package Utils;

import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static org.junit.Assert.*;

/**
 * Created by yosid on 16/11/2017.
 */

public class NetworkUtilsTest {
    public static final String TAG = "NetworkUtilsTest";
    NetworkUtils networkUtils;
    HttpURLConnection connection;
    InputStream inputStream;

    @Before
    public void setUp(){
        networkUtils = new NetworkUtils();

    }

    @Test
    public void testConnect() throws Exception {

        connection = networkUtils.connect("http://www.google.com");
        assertNotNull(connection);
        assertEquals(3000,connection.getConnectTimeout());
        assertEquals(3000,connection.getReadTimeout());
        assertEquals("GET", connection.getRequestMethod());
        assertTrue( connection.getDoInput());
    }

    @Test
    public void testGetResponse() throws Exception{
        connection = networkUtils.connect("http://www.google.com");
        String response = networkUtils.getResponse(connection);
        Log.d(TAG, "testGetResponse: "+response);
        assertTrue(response.contains("html"));

    }

    @Test
    public void testGetURL() throws Exception{
        final StringBuilder response =new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, "testGetURL: "+year+month+day);
        int token = year+month+day;
        NetworkUtils.ResponseListener<String> listener =
                new NetworkUtils.ResponseListener<String>() {
                    @Override
                    public void downloadStarted() {
                        synchronized (this){
                            try {
                                wait(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void finishedDownloading(String result) {
                        response.append(result);
                        synchronized (this){
                            notifyAll();
                        }
                    }
                };

        String loginUrl = "http://crm.meirkids.co.il/api/app_login.asp?CrmID=2953&Token="+token;
        String watchingUrl ="http://crm.meirkids.co.il/api/app_show.asp?LessonID=5969102&CrmID=2953&Token="+token;
        networkUtils.doGet(loginUrl,listener);
        networkUtils.doGet(watchingUrl,listener);
        assertEquals("truetrue",response.toString());
    //    assertTrue(response.toString().contains("html"));
    }
}
