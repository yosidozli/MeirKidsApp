package com.yosidozli.meirkidsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.yosidozli.meirkidsapp.dialogs.ForceUpdateDialog;
import com.yosidozli.meirkidsapp.dialogs.UpdateDialog;

import Utils.HttpGetTask;

public class SplashActivity extends AppCompatActivity implements UpdateDialog.UpdateDialogListener, ForceUpdateDialog.ForceUpdateDialogListener {
    private static final String TAG ="SplashActivity";
    private static String VERSION_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VERSION_URL = getString(R.string.url_check_version);
        checkForVersionAndStartMainActivity();


    }

    private void checkForVersionAndStartMainActivity(){
        new VersionQueryTask().execute(VERSION_URL);

    }

    private void showUpdateDialog(){
        DialogFragment newFragment = new UpdateDialog();
        newFragment.show(getSupportFragmentManager(),"update_dialog");

    }

    private void showForceUpdateDialog(){
        DialogFragment newFragment = new ForceUpdateDialog();
        newFragment.show(getSupportFragmentManager(),"force_update_dialog");

    }

    public static String getApplicationName(Context context) {
        String name =context.getApplicationInfo().packageName;
        //Log.d(TAG, "getApplicationName: "+name);
        return name ;
    }

    @Override
    public void onForceDialogPositiveClick(DialogFragment dialog) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+getApplicationName(this))));
        finish();
    }

    @Override
    public void onForceDialogNegativeClick(DialogFragment dialog) {
        finish();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+getApplicationName(this))));
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    private class VersionQueryTask extends HttpGetTask{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);




            float thisVersion = Float.parseFloat( getString(R.string.app_data_version));
            float lastVersionApproved = 0 ;
            //Log.d(TAG, "onPostExecute: this version: "+thisVersion);
            try {
                int i;
                for (i=0 ; i<s.length();  i++) {
                    char isDigit = s.charAt(i);
                    if(Character.isDigit(isDigit)) {
                        s = s.substring(i);
                        break;
                    }

                }
                lastVersionApproved = Float.parseFloat(s);
                //Log.d(TAG, "onPostExecute: last Approved version: "+lastVersionApproved);

            }catch (NumberFormatException e){
                Log.e(TAG, "onPostExecute: error in reading lastVersion",e );
            }catch (NullPointerException e){
                Log.e(TAG, "onPostExecute: lastVersionApproved string is null",e );
            }
            checkVersionAndNavigate(thisVersion,lastVersionApproved);

        }

        private void checkVersionAndNavigate(Float thisVersion, Float lastApprovedVersion){

            if(thisVersion >= lastApprovedVersion){
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            } else if( (lastApprovedVersion.intValue() - thisVersion.intValue()) >0){
                //Log.d(TAG, "checkVersionAndNavigate: forceUpgrade");
                showForceUpdateDialog();


            } else {
                //Log.d(TAG, "checkVersionAndNavigate: recommend Update");
                showUpdateDialog();
            }


        }
    }




}
