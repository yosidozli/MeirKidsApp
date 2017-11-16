package Utils;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


/**
 * Created by yosid on 16/11/2017.
 */

public class NetworkUtils implements Runnable {
    private ResponseListener responseListener;
    private HttpURLConnection connection;
    private InputStream inputStream;
    private String urlString;

    public NetworkUtils(){
        super();
    }

    /**
     * Returns a HttpURLConnection for the specified url
     *
     * @param urlString the url
     * @return HttpURLConnection linking to the specified url
     * @throws Exception  IOException  if an I/O exception occurs
     */
    public HttpURLConnection connect(String urlString) throws Exception{

        URL url = null;

        url = new URL(urlString);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.setDoInput(true);
        return connection;
        //todo add callback with progress connectionOK
    }

    public String getResponse(HttpURLConnection connection) throws IOException {
        inputStream = connection.getInputStream();
        return readInput(inputStream);

    }

    private String readInput(InputStream inputStream){
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("///A");
        return scanner.next();
    }

    public void doGet(String urlString, ResponseListener responseListener){
        this.urlString = urlString;
        this.responseListener = responseListener;
        new Thread(this).run();
        responseListener.downloadStarted();

    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = connect(urlString);
            String response ="";
            response = getResponse(connection);
            responseListener.finishedDownloading(response);
        } catch (Exception e) {
            responseListener.finishedDownloading(e.getMessage());
        }finally {
            if(inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    responseListener.finishedDownloading(e.getMessage());
                }
            if(connection != null)
                connection.disconnect();
        }


    }

    interface ResponseListener<T> {
        public void downloadStarted();
        public void finishedDownloading(T result);
    }





}
