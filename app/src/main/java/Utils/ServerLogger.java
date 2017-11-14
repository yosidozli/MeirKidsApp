package Utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by yosid on 19/10/2017.
 */

public class ServerLogger   {
    public static final String TAG = "ServerLogger";
    public static final String LOG_FAILED = "log failed";
    private String serverIp;
    private int status;
    public ServerLogger(String serverIp) {
        this.serverIp = serverIp;
    }

    public void logUser(String id ,String ip, Callback callback) throws Exception{
        String logAddress = serverIp;
        logAddress =  Uri.parse(logAddress).buildUpon().appendQueryParameter("CrmID",id).appendQueryParameter("IP",ip).build().toString();
        Log.d(TAG, "logUser: "+logAddress);
        URL url = new URL(logAddress);
        NetworkRunnable runnable = new NetworkRunnable(url,callback);
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join(3000);
        Log.d(TAG, "logUser: "+runnable.getResponse());
        Log.d(TAG, "logUser: "+runnable.getResponse().equals(String.valueOf(true)));


        callback.onSuccess();
    }

    private class NetworkRunnable implements Runnable{

        private URL url;
        private String response;
        HttpURLConnection connection;
        InputStream is;
        Callback callback;



        public NetworkRunnable(URL url , Callback callback ) {
            this.url = url;
            this.callback = callback;
        }

        @Override
        public void run()   {

            try {
                updateServer(url , callback);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void updateServer(URL url ,Callback callback) throws IOException{
            try {
                connection  = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                is  = connection.getInputStream();
                String response;
                response = readData(is);
                if(!response.equals("True"))
                    callback.onFail();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(is != null)
                    is.close();
                if(connection  != null)
                    connection.disconnect();
            }



        }

        private String readData(InputStream is){
            Scanner scanner = new Scanner(is);
            scanner.useDelimiter("\\\\A");
            if(scanner.hasNext())
              response = scanner.next();
            return response;

        }
        public String getResponse() {
            return response;
        }
    }



}
