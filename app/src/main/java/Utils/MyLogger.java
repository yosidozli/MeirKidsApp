package Utils;

import android.net.Uri;

import java.util.Calendar;

/**
 * Created by yosid on 17/11/2017.
 */

public class MyLogger {
    private static final String LOGIN_URL ="http://crm.meirkids.co.il/api/app_login.asp";
    private static final String CRM_ID_PARAM ="CrmID";
    private static final String TOKEN_PARAM ="Token";

    private static final String  LESSON_WATCHED_URL="http://crm.meirkids.co.il/api/app_show.asp";
    private static final String LESSON_ID_PARAM = "LessonID";

    public String getUrl() {
        return url;
    }

    private String url ="";

    protected static int getToken(){
        int token = 0;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        token = year+month+day;
        return token;
    }

    NetworkUtils networkUtils;
    public MyLogger(){
        networkUtils = new NetworkUtils();
    }

    public void logUserLogin(String crmID){
        url = "";
        url = Uri.parse(LOGIN_URL).buildUpon().
                appendQueryParameter(CRM_ID_PARAM,crmID).
                appendQueryParameter(TOKEN_PARAM,String.valueOf( getToken()))
                .build().toString();
        networkUtils.doGet(url);

    }

    public void logLessonChosen(String crmID , String lessonID){
        url ="";
        url = Uri.parse(LESSON_WATCHED_URL).buildUpon().
                appendQueryParameter(LESSON_ID_PARAM,lessonID).
                appendQueryParameter(CRM_ID_PARAM,crmID).
                appendQueryParameter(TOKEN_PARAM, String.valueOf(getToken()))
                .build().toString();
        networkUtils.doGet(url);

    }

}
