package uk.echosoft.garage.opener;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class GarageOpenerWidget extends AppWidgetProvider {

    public static final String TOGGLE_GARAGE_ACTION = "toggleGarage";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("toggleGarage".equals(intent.getAction())) {
            Intent loadState = new Intent(context, ToggleGarageService.class);
            loadState.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS));
            context.startService(loadState);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), uk.echosoft.garage.opener.R.layout.garage_opener_widget);
        Intent toggleGarageIntent = new Intent(context, GarageOpenerWidget.class);
        toggleGarageIntent.setAction(TOGGLE_GARAGE_ACTION);
        toggleGarageIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, toggleGarageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(uk.echosoft.garage.opener.R.id.widget_button_garage_opener, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        // There may be multiple widgets active, so update all of them
        Intent loadState = new Intent(context, LoadStateService.class);
        loadState.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.startService(loadState);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

