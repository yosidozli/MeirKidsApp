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

public class ServerLogger {
    public static final String TAG = "ServerLogger";



    public static final String LOG_FAILED = "log failed";
    private String serverIp;

    public ServerLogger(String serverIp) {

        this.serverIp = serverIp;


    }

    public void logUser(String id ,String ip) throws Exception{
        String logAddress = serverIp;
        logAddress =  Uri.parse(logAddress).buildUpon().appendQueryParameter("CrmID",id).appendQueryParameter("IP",ip).build().toString();
        Log.d(TAG, "logUser: "+logAddress);
        URL url = new URL(logAddress);
        NetworkRunable runable = new NetworkRunable(url);
        Thread thread = new Thread(runable);
        thread.start();
        thread.join();
        Log.d(TAG, "logUser: "+runable.getResponse());
        Log.d(TAG, "logUser: "+runable.getResponse().equals(String.valueOf(true)));

        if(!runable.getResponse().equals("True"))
            throw new Exception(LOG_FAILED);
    }

    private class NetworkRunable implements Runnable{

        private URL url;
        private String response;
        HttpURLConnection connection;
        InputStream is;



        public NetworkRunable(URL url ) {
            this.url = url;

        }

        @Override
        public void run()   {

            try {
                updateServer(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void updateServer(URL url) throws IOException{
            try {
                connection  = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                is  = connection.getInputStream();
                readData(is);

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
