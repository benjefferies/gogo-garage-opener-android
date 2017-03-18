package uk.echosoft.garage.opener;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;
import android.widget.Toast;

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
        SharedPreferences authentication = context.getSharedPreferences("authentication", 0);
        String authToken = authentication.getString("authToken", "");
        SharedPreferences settings = context.getSharedPreferences("settings", 0);
        String uri = settings.getString("uri", "");
        if ("".equals(uri) || "".equals(authToken)) {
            Toast.makeText(context, "Configure settings and login", Toast.LENGTH_LONG).show();
            Intent settingsIntent = new Intent(context, SettingsActivity.class);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent loginIntent = new Intent(context, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            TaskStackBuilder.create( context )
                    .addNextIntent(loginIntent)
                    // use this method if you want "intentOnTop" to have it's parent chain of activities added to the stack. Otherwise, more "addNextIntent" calls will do.
                    .addNextIntentWithParentStack(settingsIntent)
                    .startActivities();
        }
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

