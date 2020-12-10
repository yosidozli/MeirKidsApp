package com.yosidozli.meirkidsapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yosidozli.meirkidsapp.dialogs.ConnectDialog;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import Utils.AnalyticsUtils;
import Utils.MyLogger;


public class MainActivity extends AppCompatActivity implements AboutFragment.OnFragmentInteractionListener,
        LessonAdapter.ListItemClickListener,LessonSetAdapter.ListItemClickListener
,ConnectDialog.ConnectDialogListener{

    private final static String TAG = "MainActivity";
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL ="https://meirkids.co.il/temp/xmlLessonesKids/lessons.asp";//?ContentType=1";// "http://meirkids.co.il/Tools/TopContentsToMeirtvX/?setType=0&contentType=1&count=5000";
    private static final String SETS_URL ="https://meirkids.co.il/temp/xmlLessonesKids/sets.asp";
    private static final int USER_TAB_INT = 2;
    private static final int SETS_TAB_INT = 1;
    private static final int LAST_TAB_INT = 0;
    private static final int LOGIN_REQUEST = 1;
    private static final String LAST_POSITION = "last_position";
    private static final String APPROVED_USER = "approved user";
    private static final String PROMPT_TO_REGISTER ="promp_to_register";
    private static  final  String CURRENT_TAB ="current_tab";
    private static final    String FRAGMENT_TAG = "frag_tag";
    private static final String NUMBER_TO_QUERY ="20";




    private PreferencesUtils prefUtils;

    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    public static String sPref = ANY;
    private User mUser = null;
    private  boolean approvedUser =false;
    private boolean prompt_to_connect = true;



    private Button mButton;
    private List<Lesson> lessons;
    private List<Lesson> selectedSetLessons;
    private List<LessonSet> lessonSets;
    static List<Lesson> staticLesson = new ArrayList<>();
    private LessonAdapter mLessonAdapter;
    private RecyclerView mLessonsList;
    private LessonSetAdapter mLessonSetAdapter;
    private View mFragment;
    private int currentTab;
    private ConstraintLayout mConstraintLayout;
    private MenuItem mLoginMenuItem;
    private SharedPreferences pref;
    private int lastPosition;
    private Cache mCache;
    private String chosedLessonTitle;
    private FirebaseAnalytics mFirebaseAnalytics;
//    private LoginTask mLoginTask;
    private DownloadLessonsXmlTask mDownloadLessonXmlTask;
    private AnalyticsUtils mAnalyticsUtils;
    private MyLogger logger;

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent =new Intent(this,VideoActivity.class);
       // Log.d(TAG, "onListItemClick: "+currentTab);
        chosedLessonTitle = null;
        if(currentTab == LAST_TAB_INT) {
            /*intent.putExtra("Lesson", lessons.get(clickedItemIndex));
            staticLesson = lessons;
            startActivity(intent);*/
            if(mDownloadLessonXmlTask != null && !mDownloadLessonXmlTask.isCancelled())
                mDownloadLessonXmlTask.cancel(false);
            chosedLessonTitle = lessons.get(clickedItemIndex).getTitle();
            mAnalyticsUtils.logLesson(lessons.get(clickedItemIndex));
            if(mUser!= null )
                logger.logLessonChosen(String.valueOf(mUser.getPersonId()),lessons.get(clickedItemIndex).getId());
            mDownloadLessonXmlTask =  new DownloadLessonsXmlTask();
           mDownloadLessonXmlTask.execute(URL,lessons.get(clickedItemIndex).getLessonSetID());

        }
        else if(currentTab == SETS_TAB_INT) {
            if(mDownloadLessonXmlTask != null && !mDownloadLessonXmlTask.isCancelled())
                mDownloadLessonXmlTask.cancel(false);
            chosedLessonTitle = null;
            mAnalyticsUtils.logSet(lessonSets.get(clickedItemIndex));
            mDownloadLessonXmlTask =  new DownloadLessonsXmlTask();
            mDownloadLessonXmlTask.execute(URL,lessonSets.get(clickedItemIndex).getID());
        }
       // Toast.makeText(this,getString(R.string.toast_downloading_lessons),Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy(){
        if(mUser != null) {
        prefUtils.putUserInPreferences(mUser);
        }


        super.onDestroy();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAnalyticsUtils = new AnalyticsUtils(this);
        logger = new MyLogger();

        ;//.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.bar_logo));

        setTitleBarImage();
        forceRTLIfSupported();
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if(savedInstanceState != null) {
            approvedUser = savedInstanceState.getBoolean(APPROVED_USER);
            prompt_to_connect = savedInstanceState.getBoolean(PROMPT_TO_REGISTER);
            lastPosition = savedInstanceState.getInt(LAST_POSITION);
            currentTab = savedInstanceState.getInt(CURRENT_TAB);

        }
        prefUtils = new PreferencesUtils(this);
        if(mUser == null) {
            mUser = prefUtils.getUserFromPreferences();
        if(mUser == null)
            showConnectDialog();
            //todo check if loginTask response to not internet or error
            //check if current user is activated
//            if(!approvedUser && prompt_to_connect) {
//                mLoginTask =  new LoginTask();
//                mLoginTask.execute(mUser);
//            }
//            /*todo check if i have a logic error, because maybe im counting on the asynchronic response from login task
//              */
//            if(mUser != null) {
//                prefUtils.putUserInPreferences(mUser);
//            }

        }


            loadPage();


    }

    private void setTitleBarImage(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.drawable.bar_logo);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER           );
        layoutParams.leftMargin=40;
        layoutParams.rightMargin=40;


        imageView.setLayoutParams(layoutParams);
        actionBar.setTitle(null);
        actionBar.setCustomView(imageView);
    }




    //return ture if android is connected to network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Uses AsyncTask to download the XML feed from stackoverflow.com.
    public void loadPage() {
        mobileConnected = isNetworkAvailable();
        if((sPref.equals(ANY)) && (wifiConnected || mobileConnected)) {
            new DownloadXmlTask().execute(URL,null,NUMBER_TO_QUERY);
            new DownloadSetsXMLTask().execute(SETS_URL);
        }
        else if ((sPref.equals(WIFI)) && (wifiConnected)) {
            new DownloadXmlTask().execute(URL,null,NUMBER_TO_QUERY);
            new DownloadSetsXMLTask().execute(SETS_URL);
        } else {
            // show error
            setContentView(R.layout.error_loading_data_layout);
            mButton = (Button) findViewById(R.id.error_loading_button);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadPage();
                }
            });

        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if(mUser != null)
            approvedUser = true;
        else
            approvedUser = false;
        outState.putBoolean(APPROVED_USER,approvedUser);
        outState.putBoolean(PROMPT_TO_REGISTER,prompt_to_connect);
        if(mLessonsList != null)
            lastPosition = ((LinearLayoutManager)mLessonsList.getLayoutManager()).findFirstVisibleItemPosition();
        else
            lastPosition = 0;
        outState.putInt(LAST_POSITION,lastPosition);
        outState.putInt(CURRENT_TAB,currentTab);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if(fragment != null) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

        super.onSaveInstanceState(outState);


    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(mLoginTask != null)
//            mLoginTask.cancel(true);
//        mLoginTask = null;
    }

//    //todo check if needed to be deleted
//    @Override
//    public void finishedLogin(User user) {
//        if(user != null)
//      //  Log.d(TAG, "finishedLogin: "+user.getFirstName()+" "+user.getLastName());
//        mUser = user;
//        if(mUser != null)
//            approvedUser = true;
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivityForResult(intent,LOGIN_REQUEST);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        prompt_to_connect =false;
    }

    @Override
    public void onDialogNetuarlClick(DialogFragment dialog) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.url_registration)));
        startActivity(intent);
    }


    private void showConnectDialog(){
        DialogFragment newFragment = new ConnectDialog();
        newFragment.show(getSupportFragmentManager(),"connect_dialog");

    }


    private void downloadTaskOnPostExecute(List<Lesson> result){
        if (result == null) {
            setContentView(R.layout.error_loading_data_layout);
            if (mButton == null){
                mButton = (Button) findViewById(R.id.error_loading_button);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadPage();
                    }
                });
            }
            return;
        }

        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        MainActivity.this.lessons = result;
        mLessonsList = (RecyclerView) findViewById(R.id.lesson_rv);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mLessonsList.setLayoutManager( new LinearLayoutManager(MainActivity.this));
        else
            mLessonsList.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        mLessonsList.setHasFixedSize(true);

        /*TODO temp to delete after */
        staticLesson.addAll(result);
        /*                     */
        mLessonAdapter = new LessonAdapter(result,MainActivity.this, R.layout.item_content_layout);


        mLessonsList.setAdapter(mLessonAdapter);
        ((LinearLayoutManager)mLessonsList.getLayoutManager()).scrollToPosition(lastPosition);



          /*  FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.user_fragment, AboutFragment.newInstance(mUser),FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(null).commit();*/

        final TabLayout tabLayout= (TabLayout) findViewById(R.id.tabLayout);
        //TODO replace with resources string or icon
        tabLayout.addTab(tabLayout.newTab().setText("אחרונים"));
        //TODO replace with resources string or icon
        tabLayout.addTab(tabLayout.newTab().setText("סדרות"));
        //TODO replace with resources string or icon
        tabLayout.addTab(tabLayout.newTab().setText("אודות"));
        setUserLayout(false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {


            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Animation outToLeft = MyAnimation.fadeOutAnimation();
                outToLeft.setDuration(250);
                final Animation inAnimation =MyAnimation.fadeinAnimation();
                inAnimation.setDuration(250);




                //TODO change equals to constatnt
                if(tab.getPosition() == SETS_TAB_INT) {
                    // Log.d(TAG, "onTabSelected: "+tab.getText());
                    //  Log.d(TAG, "onTabSelected: "+mLessonSetAdapter);


                    outToLeft.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            mLessonsList.setAnimation(inAnimation);
                            mLessonsList.setAdapter(mLessonSetAdapter);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });



                    if(currentTab == LAST_TAB_INT || currentTab ==SETS_TAB_INT) {
                        mLessonsList.startAnimation(outToLeft);
                        // mLessonsList.setAdapter(mLessonSetAdapter);
                    }
                    else {
                        mLessonsList.setAnimation(MyAnimation.inFromRightAnimation());
                        mLessonsList.setAdapter(mLessonSetAdapter);
                    }
                    currentTab = SETS_TAB_INT;
                }
                else if (tab.getPosition() == 0) {

                    outToLeft.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mLessonsList.setAnimation(inAnimation);
                            mLessonsList.setAdapter(mLessonAdapter);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });


                    if(currentTab == SETS_TAB_INT )
                        mLessonsList.startAnimation(outToLeft);
                    else {
                        mLessonsList.setAnimation(MyAnimation.inFromRightAnimation());
                        mLessonsList.setAdapter(mLessonAdapter);
                    }
                    currentTab = LAST_TAB_INT;
                }
                else if(tab.getPosition()== 2){

                    setUserLayout(true);

                    currentTab = USER_TAB_INT;
                }
            }



            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                Animation leftListOutAnimation = MyAnimation.outToLeftAnimation();
                Animation RightListOutAnimation = MyAnimation.outToRightAnimation();


                if(tab.getPosition() == SETS_TAB_INT) {
                    //  Log.d(TAG, "onTabSelected: "+tab.getText());
                    //  Log.d(TAG, "onTabSelected: "+mLessonSetAdapter);
                    mLessonsList.setAnimation(RightListOutAnimation);

                }
                else if (tab.getPosition() == LAST_TAB_INT) {


                    mLessonsList.setAnimation(RightListOutAnimation);

                }
                else if(tab.getPosition()== USER_TAB_INT){
                    setUserLayout(false);
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(currentTab).select();
    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String,  Void, List<Lesson> > {

        @Override
        protected List<Lesson> doInBackground(String... urls) {
            return downloadXmlTaskDoInBackground(urls);
        }
        @Override
        protected void onPostExecute(List<Lesson> result){
            downloadTaskOnPostExecute(result);
        }
    }

    private List<Lesson> downloadXmlTaskDoInBackground(String[] urls) {
        List<Lesson> lessons;
        try {
            if(mCache == null) {
                mCache = new Cache(MainActivity.this);
                mCache.initializePath();
            }

            lessons = mCache.getList(urls.length>1 && urls[1]!=null? urls[1]:"LAST");
            if(lessons != null) {
                //Log.d(TAG, "doInBackground: got lessons from cahce :"+ (urls.length>1? urls[1]:"LAST"));
                return lessons;

            }

            lessons = loadXmlFromNetwork(urls[0], urls.length>1? urls[1]:null, urls.length>2? urls[2]: null);
            //Log.d(TAG, "doInBackground: got lessons from web :"+ (urls.length>1? urls[1]:"LAST"));
            if(mCache!= null)
                mCache.writelist(lessons, urls.length>1 && urls[1]!=null? urls[1]:"LAST");
            return lessons;
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ",e );
            return null;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "doInBackground: " , e);
            return null;
        }
    }

    private List<Lesson> loadXmlFromNetwork(String urlString, String setId, String count) throws XmlPullParserException, IOException {
        XmlLessonParser lessonParser = new XmlLessonParser();
        if(setId != null) {
            urlString = Uri.parse(urlString).buildUpon().appendQueryParameter("SetId", setId).build().toString();
        }
        if(count != null){
            urlString = Uri.parse(urlString).buildUpon().appendQueryParameter("Count", count).build().toString();
        }
        try (InputStream stream = downloadUrl(urlString)) {
            return lessonParser.parse(stream);
        }
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        String fullUrl = Uri.parse(urlString).buildUpon().appendQueryParameter("ContentType","1").build().toString();
        URL url = new URL(fullUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    private void setUserLayout(boolean show){
        mLessonsList.setVisibility(show ? View.GONE: View.VISIBLE);
        if(mFragment == null)
            mFragment = findViewById(R.id.user_fragment);
        mFragment.setVisibility((!show ? View.GONE: View.VISIBLE));
        if(mConstraintLayout == null)
            mConstraintLayout = (ConstraintLayout) findViewById(R.id.main_activity_layout);


        if(show){

            mFragment.setAnimation(MyAnimation.inFromLeftAnimation());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.user_fragment,new AboutFragment(),FRAGMENT_TAG);
            fragmentTransaction.addToBackStack(null).commit();
            }
        else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment frag = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
            if(frag != null) {
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(frag).commit();

            }
            mFragment.setAnimation(MyAnimation.outToLeftAnimation());


        }








    }





    class DownloadLessonsXmlTask extends DownloadXmlTask{
        @Override
        protected void onPostExecute(List<Lesson> result){
            downloadLessonsXmlTaskOnPostExecute(result);
        }
    }

    private void downloadLessonsXmlTaskOnPostExecute(List<Lesson> result) {
        staticLesson = result;
        int lessonIndex =0;

        if(chosedLessonTitle != null){
            for(int  i=0;staticLesson != null && i<staticLesson.size() ;i++){
                if(staticLesson.get(i).getTitle().compareTo(chosedLessonTitle) ==0 )
                    lessonIndex = i;
            }
        }
        // todo show error massage if staticLesson is null;
        try {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            intent.putExtra("Lesson", staticLesson.get(lessonIndex));
            startActivity(intent);
        }
        catch (NullPointerException e) {
            Log.e(TAG, "onPostExecute: ", e );
            Toast.makeText(MainActivity.this,getString( R.string.error_no_internet),Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        mLoginMenuItem = menu.findItem(R.id.menu_login);
        if(mUser != null ) {


            String string = getString(R.string.menu_logout);
            mLoginMenuItem.setTitle(string);

        }
        else
            mLoginMenuItem.setTitle(getString(R.string.menu_login));
        return super.onCreateOptionsMenu(menu);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_login:{
                if(mUser != null ) {
                    //Todo change to string resources
                    Toast.makeText(MainActivity.this,getString( R.string.toast_logout,mUser.getFirstName()),Toast.LENGTH_LONG).show();
                    mUser = null;
                    prefUtils.putUserInPreferences(mUser);
                    item.setTitle(getString(R.string.menu_login));
                    if(currentTab == USER_TAB_INT){
                        if(mFragment != null)
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.user_fragment,new AboutFragment()).commit();
                    }
                } else{
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST);
                }



                break;
            }
            case R.id.menu_register:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.url_registration)));
                startActivity(intent);
                break;


            }
            case R.id.menu_contact_us:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.url_contact)));
                startActivity(intent);
                break;
            }
            case  R.id.menu_meirkids_web:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.url_meirkids_website)));
                startActivity(intent);
                break;
            }
            case  R.id.menu_privacy_policy:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.url_policy)));
                startActivity(intent);
                break;
            }

        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST){
            if(resultCode == AppCompatActivity.RESULT_OK){
                mUser = prefUtils.getUserFromPreferences();
                mLoginMenuItem.setTitle(getString(R.string.menu_logout));
                Toast.makeText(MainActivity.this,getString( R.string.toast_login,mUser.getFirstName()),Toast.LENGTH_LONG).show();
                prompt_to_connect = false;
                //update user screen
                Bundle bundle = new Bundle();
                bundle.putString("user_id",String.valueOf(mUser.getPersonId()));
                //bundle.putString("user_name", mUser.getFirstName()+" "+mUser.getLastName());
                DateFormat df = SimpleDateFormat.getDateInstance();

                bundle.putString("user_connection_date",df.format(new Date()));
                mFirebaseAnalytics.logEvent("user_login", bundle);
                logger.logUserLogin(String.valueOf(mUser.getPersonId()));


            }


        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }



    private class DownloadSetsXMLTask extends AsyncTask<String,  Void, List<LessonSet> > {
        @Override
        protected List<LessonSet> doInBackground(String... urls) {
            List<LessonSet> sets = null;

                try {
                    if(mCache != null){

                        sets =mCache.getList("SETS");

                    }

                    if(sets != null) {
                      //  Log.d(TAG, "doInBackground: got sets from cahce :SETS");
                        return sets;

                    }

                    sets = loadXmlFromNetwork(urls[0]);
                    if(mCache != null)
                        mCache.writelist(sets,"SETS");
                   return sets;

                } catch (IOException e) {
                   // Log.d(TAG, "doInBackground: ",e);
                    return null;
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "doInBackground: ", e);
                    return null;
                }


        }



        @Override
        protected void onPostExecute(List<LessonSet> result) {

            lessonSets = result;
            mLessonSetAdapter = new LessonSetAdapter(result,MainActivity.this, R.layout.set_content_layout);
            if(currentTab == SETS_TAB_INT) {
                mLessonsList.setAdapter(mLessonSetAdapter);
                ((LinearLayoutManager)mLessonsList.getLayoutManager()).scrollToPosition(lastPosition);
            }



        }

        private List<LessonSet> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
            InputStream stream = null;
            // Instantiate the parser
            XmlLessonSetParser lessonSetParser = new XmlLessonSetParser();




            try {

                stream = downloadUrl(urlString);
             //   Log.d(TAG,"loadXmlFromNetwork: "+urlString);
                lessonSets = lessonSetParser.parse(stream);
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }

            return lessonSets;
        }

        // Given a string representation of a URL, sets up a connection and gets
// an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            String fullUrl = Uri.parse(urlString).buildUpon().appendQueryParameter("ContentType","1").appendQueryParameter("Count","500").build().toString();
            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }


    }

//    class LoginTask extends AsyncTask<User,Void,Boolean>{
//
//
//
//        @Override
//        protected Boolean doInBackground(User[] params){
//            User user = (User) params[0];
//            if(user == null)
//                return false;
//            UserParser mUserParser = UserParser.getInstance();
//            mUserParser.setUserName(user.getUserName());
//            mUserParser.setPassword(user.getPassword());
//            InputStream inputStream = null;
//
//            try{
//                inputStream = downloadUserUrl(getString(R.string.url_user_validation),user);
//
//                if( (user = mUserParser.validateUser(inputStream)) != null) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("user_id",String.valueOf(user.getPersonId()));
//                    //bundle.putString("user_name", user.getFirstName()+" "+user.getLastName());
//                    DateFormat df = SimpleDateFormat.getDateInstance();
//                    bundle.putString("user_connection_date",df.format (new Date()));
//                    mFirebaseAnalytics.logEvent("user_login", bundle);
//                    logger.logUserLogin(String.valueOf(mUser.getPersonId()));
//
//
//                    return true;
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "doInBackground: ",e );
//                return false;
//            } finally {
//                if(inputStream != null)
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        Log.e(TAG, "doInBackground: ",e );
//
//                    }
//            }
//
//            return false;
//        }
//        @Override
//        protected void onPostExecute(Boolean result){
//            if(result){
//               mUser.setApproved(true);
//                prefUtils.putUserInPreferences(mUser);
//                //TODO change text to string resource
//                Toast.makeText(MainActivity.this,getString( R.string.toast_login,mUser.getFirstName()),Toast.LENGTH_LONG).show();
//            }else {
//                //TODO change text to string resource
//                if(prompt_to_connect) {
//                    showConnectDialog();
//
//                }
//            }
//            mLoginTask =  null;
//
//            invalidateOptionsMenu();
//
//
//        }
//
//        private InputStream downloadUserUrl(String validationUrl,User user) throws IOException {
//            String fullUrl = Uri.parse(validationUrl).buildUpon().appendPath(user.getUserName())
//                    .appendPath(user.getPassword()).toString();
//            URL url = new URL(fullUrl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000 /* milliseconds */);
//            conn.setConnectTimeout(15000 /* milliseconds */);
//            conn.setRequestMethod("GET");
//            conn.setDoInput(true);
//            // Starts the query
//            conn.connect();
//            return conn.getInputStream();
//        }
//
//
//
//
//    }

    

}
