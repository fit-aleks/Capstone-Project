package com.fitaleks.walkwithme.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;

import com.fitaleks.walkwithme.HistoryFragment;
import com.fitaleks.walkwithme.MainActivity;
import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeDatabase;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by alexander on 24.06.16.
 */
public class TodaySummaryWidgetIntentService extends IntentService {

    private static final String[] HISTORY_BY_DAY_COLUMNS = {
            WalkWithMeDatabase.HISTORY + "." + FitnessHistory.ID,
            FitnessHistory.DATE,
            "SUM("+ FitnessHistory.NUM_OF_STEPS +")",
    };

    private static final int COL_ID = 0;
    private static final int COL_DATE = 1;
    private static final int COL_NUM_OF_STEPS = 2;

    public TodaySummaryWidgetIntentService() {
        super("TodaySummaryWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodaySummaryWidgetProvider.class));
        final Cursor data = getContentResolver().query(WalkWithMeProvider.History.BY_DAYS_URI,
                HISTORY_BY_DAY_COLUMNS, null, null, FitnessHistory.DATE + " DESC ");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }
        int stepCount = data.getInt(COL_NUM_OF_STEPS);
        final String stepCounts = stepCount > 0
                ? getString(R.string.step_counter_title, stepCount)
                : getString(R.string.no_steps_yet);
        data.close();
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_today_summary);
            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, stepCounts);
            }
            views.setTextViewText(R.id.widget_steps_count, stepCounts);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}
