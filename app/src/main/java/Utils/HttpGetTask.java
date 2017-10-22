package Utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by yosid on 28/06/2017.
 */

public class HttpGetTask extends AsyncTask <String ,Void,String> {
    private static final String TAG = "HTTP_GET_TASK";
    private String result;
    
    

    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream = null;
        String urlContent = null;
        try {
            
            inputStream = downloadUrl(new URL(Uri.parse(params[0]).toString()));
            urlContent = readInput(inputStream);
            
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e );
        }

        finally {
            if(inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "readInput: ",e );
                }
        }
        return urlContent;
    }
    


    private InputStream downloadUrl(URL url) throws IOException{
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }
    
    private String readInput(InputStream inputStream){
        String str = null;
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        try {
            str = scanner.next();
        } catch( NoSuchElementException e){
            str = "";
        }

        return str;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        result = s;
    }

    public String getResult() {
        return result;
    }
}
