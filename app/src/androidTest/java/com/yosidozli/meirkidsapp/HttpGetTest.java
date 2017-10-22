package com.yosidozli.meirkidsapp;

/**
 * Created by yosid on 19/10/2017.
 */

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;
import org.mockito.*;
import org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URL;
import java.util.Scanner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.*;

import Utils.HttpGetTask;
import Utils.ServerLogger;

@RunWith(AndroidJUnit4.class)
public class HttpGetTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Ignore
    @Test
    public void testHttpConnection(){
        HttpGetTask task = new HttpGetTask();
        task.execute("http://meirkids.co.il/temp/xmlLessonesKids/lessons.asp?count=1");
        synchronized (this){
            try {
                wait(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String result = task.getResult();
        assertTrue(result.length()>10);
    }

    @Test
    public void testServerUserLogin(){
        String crmId = "5";
        String IP = "111.111.111.111";
        String serverIp ="http://crm.meirkids.co.il/api/app_login.asp";
        ServerLogger serverLogger = new ServerLogger(serverIp);
        try {
            serverLogger.logUser(crmId,IP);
        } catch (Exception e) {
            fail();
        }

    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testServerLoginFailing() throws Exception{
        String crmId = "2";
        String IP = "111.111.111.111";
        String serverIp ="http://crm.meirkids.co.il/api/app_login.asp";
        ServerLogger serverLogger = new ServerLogger(serverIp);

      //  exception.expect(Exception.class);
        exception.expectMessage(ServerLogger.LOG_FAILED);
        try {
            serverLogger.logUser(crmId,IP);
        } catch (Exception e) {
            throw e;
        }

    }






}
