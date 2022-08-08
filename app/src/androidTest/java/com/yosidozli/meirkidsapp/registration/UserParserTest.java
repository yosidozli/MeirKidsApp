//package com.yosidozli.meirkidsapp.registration;
//
//import android.net.Uri;
//import android.util.Log;
//
//import org.junit.Test;
//
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import static org.junit.Assert.*;
//
//import com.yosidozli.meirkidsapp.User;
//
///**
// * Created by yosid on 24/07/2017.
// */
//public class UserParserTest {
//
//
//    @Test
//    public void validateUser() throws Exception {
//        String fullUrl = Uri.parse("https://meirkids.co.il/UserValidationServiceWebGet/UserValidationService.svc/IsUserValid").buildUpon().appendPath("צוות").appendPath("מנויים").toString();
//        URL url = new URL(fullUrl);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setReadTimeout(10000 /* milliseconds */);
//        conn.setConnectTimeout(15000 /* milliseconds */);
//        conn.setRequestMethod("GET");
//        conn.setDoInput(true);
//        // Starts the query
//        conn.connect();
//      User user =null;
//        user =  UserParser.getInstance().validateUser(conn.getInputStream());
//
//        assertEquals(user.getId(),25);
//        assertEquals(user.getLastName(),"");
//        assertEquals(user.getFirstName(),"שמור12");
//
//
//    }
//
//
//
//
//}