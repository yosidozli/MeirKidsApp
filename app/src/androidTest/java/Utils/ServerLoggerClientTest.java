package Utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;




import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by yosid on 26/10/2017.
 */







@RunWith(AndroidJUnit4.class)
public class ServerLoggerClientTest {

    private ServerLoggerClient serverLoggerC;

    @Mock
    private ServerLogger serverLogger;

    @Captor
    private ArgumentCaptor<Callback> dummyCallbackArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        serverLoggerC = new ServerLoggerClient(serverLogger);
    }

    @Test
    public void logUser() throws Exception {


        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Callback)invocation.getArguments()[2]).onSuccess();
                return null;
            }
        }).when(serverLogger).logUser(eq("1111"),eq("31312"),Mockito.any(Callback.class));

        //serverLoggerC.logUser("1111","31312");

        verify(serverLogger, times(1)).logUser(eq("1111"),eq("31312"),Mockito.any(Callback.class));

    }

    @Test
    public void logUserFailed() throws Exception {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Callback)invocation.getArguments()[2]).onFail();
                return null;
            }
        }).when(serverLogger).logUser(eq("2222"),eq("31312"),Mockito.any(Callback.class));

        try {
          //  serverLoggerC.logUser("2222","31312");
        } catch (Exception e) {

        }

        verify(serverLogger, times(1)).logUser(eq("2222"),eq("31312"),Mockito.any(Callback.class));

    }


}