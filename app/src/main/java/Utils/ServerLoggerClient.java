package Utils;

/**
 * Created by yosid on 23/10/2017.
 */

public class ServerLoggerClient implements Callback {
    private ServerLogger serverLogger;
    boolean exception = false;

    public ServerLoggerClient(String address){
        this.serverLogger = new ServerLogger(address);

    }

    public ServerLoggerClient(ServerLogger serverLogger){
        this.serverLogger = serverLogger;

    }

    public void logUser(String id ,String ip) throws Exception {

        try {
            serverLogger.logUser(id, ip,this);
            if(exception)
                throw  new Exception(ServerLogger.LOG_FAILED);
        } catch (Exception e) {
            throw e;
        }

    }


    @Override
    public void onSuccess() {

    }

    @Override
    public void onFail()  {
        exception = true;

    }
}

interface Callback{
    public void onSuccess();
    public void onFail();
}
