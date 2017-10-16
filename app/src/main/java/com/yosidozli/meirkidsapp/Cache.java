package com.yosidozli.meirkidsapp;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by yosid on 29/06/2017.
 */

public class Cache  {
    private Context mContext;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private final int MINUTES = 120;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "list_cache";
    private static final String TAG = "Cache";
    private File dirPath;

    private String todayFile;

    public Cache(Context context){
        super();
        mContext = context;

        try {
            initializePath();
        } catch (IOException e) {
            Log.e(TAG, "Cache: ",e );
        }


    }

    public synchronized void initializePath() throws IOException {

        Date nowDate = new Date();
        long nowTime = nowDate.getTime();
        dirPath = new File(mContext.getFilesDir(), DISK_CACHE_SUBDIR);
        if (!dirPath.exists())
            dirPath.mkdir();

        File[] files = dirPath.listFiles();
        for (int i = 0; i < files.length; i++) {
            long lastTime = files[i].lastModified();
            if (nowTime - lastTime > 120 * 60 * 1000) {
                files[i].delete();
                //Log.d(TAG, "initializePath: deleted"+files[i].getName());
            }
        }

    }

    public synchronized void writelist(List list, String listName)  {
        File file = new File(dirPath,listName);


        try {
            if (!file.exists()) {
                file.createNewFile();
               // Log.d(TAG, "writelist: "+listName+" created");
            }
        }catch (IOException e){
            //if file not created return
            Log.e(TAG, "writelist: ",e );
            return;
        }
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
           // Log.d(TAG, "writelist: "+"trying to write "+listName);
            oos.writeObject(list);
           // Log.d(TAG, "writelist: "+"able to write "+listName);

        } catch (IOException e) {
            Log.e(TAG, "writelist: ",e);
            //error occur and file created so delete
            if (file.exists())
                file.delete();

        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     *
     * @param listName the name of the list to read
     * @return return the list if exists. if not exists return null;
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private List  readList(String listName) throws IOException, ClassNotFoundException {
        if(listName == null)
            return null;
        //Log.d(TAG, "readList: tring to read "+listName);
        File[] files = dirPath.listFiles();
        int i =0;
        File listFile = null;

            for (i = 0; i < files.length; i++) {
                if (files[i].getName().compareTo(listName) == 0) {
                    listFile = files[i];
                    break;
                }
            }

        if (listFile == null)
            return null;


        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(listFile));

        List list = null;

        try {
           list = (List) ois.readObject();
            //Log.d(TAG, "readList: able to read "+listName);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "readList: ",e );
            throw e;

        } finally {
            ois.close();
        }

        return list;


    }

    public synchronized List getList(String listName){
        try {
             return readList(listName);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "readListIfExists: ",e );
            return null;
        } catch (IOException e) {
            Log.e(TAG, "readListIfExists: ",e );
            return null;
        } catch (NullPointerException e){
            Log.e(TAG, "getList: ",e);
            return null;
        }

    }






}
