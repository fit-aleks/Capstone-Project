package com.fitaleks.walkwithme.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fitaleks.walkwithme.StepCounterService;

/**
 * Created by alexander on 24.06.16.
 */
public class TodaySummaryWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, TodaySummaryWidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, TodaySummaryWidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (StepCounterService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, TodaySummaryWidgetIntentService.class));
        }
    }
}
