package Utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.yosidozli.meirkidsapp.Lesson;
import com.yosidozli.meirkidsapp.LessonSet;

/**
 * Created by yosid on 16/10/2017.
 */

public class AnalyticsUtils {
    Context context;
    FirebaseAnalytics mFireBaseAnalytics;
    public AnalyticsUtils(Context context){
        this.context  = context;
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logLesson(Lesson lesson){
        Bundle bundle = new Bundle();
        bundle.putString("Activity",context.getClass().getName());
        bundle.putString("LessonId",lesson.getLessonSetID());
        bundle.putString("LessonName",lesson.getTitle());
        bundle.putString("SetName",lesson.getSetName());
        mFireBaseAnalytics.logEvent("LessonChosenEvent",bundle);

    }

    public void logSet(LessonSet lessonSet){

        Bundle bundle = new Bundle();
        bundle.putString("Activity",context.getClass().getName());
        bundle.putString("SetId",lessonSet.getID());
        bundle.putString("SetName",lessonSet.getTitle());
        mFireBaseAnalytics.logEvent("SetChosenEvent",bundle);

    }


}
