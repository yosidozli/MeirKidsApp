//package com.yosidozli.meirkidsapp.registration;
//
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.NoSuchElementException;
//import java.util.Scanner;
//
///**
// * Created by yosid on 05/06/2017.
// */
//
//public class UserParser {
//
//    private final static String userValidationServiceUrl = "http://82.80.198.104/UserValidationServiceWebGet/UserValidationService.svc/IsUserValid";
//    private static UserParser instance = null;
//    private String userName;
//    private String password;
//    private LoggedInListener mLoggedInListener;
//    private User mUser= null;
//
//    private final String TAG = "USER_PARSER";
//
//    private UserParser(){
//        //to defeat instantiation
//    }
//
//    public static UserParser getInstance(){
//        if(instance == null){
//            instance = new UserParser();
//        }
//
//        return instance;
//    }
//
//
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public LoggedInListener getmLoggedInListener() {
//        return mLoggedInListener;
//    }
//
//    public void setmLoggedInListener(LoggedInListener mLoggedInListener) {
//        this.mLoggedInListener = mLoggedInListener;
//    }
//
//    public void validateUser(String userName , String password){
//        setUserName(userName);
//        setPassword(password);
//        new DownloadUrlTask().execute(userValidationServiceUrl);
//
//    }
//
//    public User getUser(){
//        return mUser;
//    }
//
//
//    public User validateUser(InputStream inputStream) {
//        long id =0;
//        long personId=0;
//        String firstName = null;
//        String lastName = null;
//        String userData;
//        Scanner scanner = new Scanner(inputStream);
//        scanner.useDelimiter("\\A");
//        try {
//           userData = scanner.next();
//        }catch (NoSuchElementException e){
//            Log.e(TAG, "validateUser: ",e );
//            return null;
//        }
//        Log.d(TAG,userData);
//
//        if(userData .compareTo( "\"null\"") ==0)
//            return null;
//
//        userData = userData.replace("\"","");
//        userData = userData.replace("\\","");
//        Log.d(TAG,userData);
//        userData = userData.replace("{","");
//        Log.d(TAG,userData);
//        userData = userData.replace("}","");
//        Log.d(TAG,userData);
//        String[] tokens = userData.split(",");
//        for(String s : tokens){
//            String keyValue[] = s.split(":");
//            switch (keyValue[0]){
//                case "ID": {
//                    id = Long.valueOf(keyValue[1]);
//                    break;
//                }
//                case "PersonID":{
//                    personId = Long.valueOf(keyValue[1]);
//                    break;
//                }
//                case "FirstName":{
//
//                    firstName = keyValue.length>1? keyValue[1]:"";
//                    break;
//                }
//                case "LastName":{
//                    lastName = keyValue.length>1? keyValue[1]:"";
//                    break;
//                }
//            }
//        }
//
//
//
//       return new User(id,personId,firstName,lastName);
//    }
//
//
//    private class DownloadUrlTask extends AsyncTask<String,  Void,User >{
//
//        @Override
//        protected User doInBackground(String... params) {
//
//            try {
//                return validateUserFromNet(params[0]);
//            } catch (IOException e) {
//                Log.d(TAG,"unable to validate user",e);
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(User user) {
//            super.onPostExecute(user);
//            mUser = user;
//            mLoggedInListener.finishedLogin(mUser);
//            if(mUser == null){
//                Log.d(TAG, "onPostExecute: userNotApproved");
//
//            }
//            else{
//                Log.d(TAG, "onPostExecute: "+user.getFirstName()+" "+user.getLastName());
//            }
//
//        }
//
//
//
//
//        private User  validateUserFromNet(String userUrl) throws IOException{
//            InputStream inputStream = null;
//            User user = null;
//            try {
//                inputStream = downloadUserUrl(userUrl);
//                user = UserParser.this.validateUser(inputStream);
//
//            }
//            finally {
//                if(inputStream != null)
//                    inputStream.close();
//            }
//            return user;
//        }
//
//
//
//        private InputStream downloadUserUrl(String validationUrl) throws IOException {
//            String fullUrl = Uri.parse(validationUrl).buildUpon().appendPath(getUserName()).appendPath(getPassword()).toString();
//            URL url = new URL(fullUrl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000 /* milliseconds */);
//            conn.setConnectTimeout(15000 /* milliseconds */);
//            conn.setRequestMethod("GET");
//            conn.setDoInput(true);
//            // Starts the query
//            conn.connect();
//            return conn.getInputStream();
//        }
//
//    }
//    public interface LoggedInListener{
//        public void finishedLogin(User user);
//    }
//
//}
