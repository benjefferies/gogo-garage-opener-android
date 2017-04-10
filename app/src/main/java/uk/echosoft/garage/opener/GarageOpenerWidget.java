package uk.echosoft.garage.opener;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class GarageOpenerWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("toggleGarage".equals(intent.getAction())) {
            new LoadStateTask(getGarageOpener(context), new WidgetClickedHandler(context)).execute();
            GarageOpener garageOpener = getGarageOpener(context);
            if (garageOpener == null) return;
            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            UpdateWidgetHandler handler = new UpdateWidgetHandler(context, appWidgetIds, true);
            ToggleGarageTask toggleGarageTask = new ToggleGarageTask(handler, garageOpener);
            toggleGarageTask.execute();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), uk.echosoft.garage.opener.R.layout.garage_opener_widget);
        Intent toggleGarageIntent = new Intent(context, GarageOpenerWidget.class);
        toggleGarageIntent.setAction("toggleGarage");
        toggleGarageIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, toggleGarageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(uk.echosoft.garage.opener.R.id.widget_button_garage_opener, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

        UpdateWidgetHandler handler = new UpdateWidgetHandler(context, appWidgetIds, false);
        new LoadStateTask(getGarageOpener(context), handler).execute();
        checkLoggedIn(context);
    }

    private void checkLoggedIn(Context context) {
        SharedPreferences authentication = context.getSharedPreferences("authentication", 0);
        String authToken = authentication.getString("authToken", "");
        SharedPreferences settings = context.getSharedPreferences("settings", 0);
        String uri = settings.getString("uri", "");
        if ("".equals(uri) || "".equals(authToken)) {
            Toast.makeText(context, "Login before using widget", Toast.LENGTH_LONG).show();
            Intent settingsIntent = new Intent(context, SettingsActivity.class);
            Intent loginIntent = new Intent(context, LoginActivity.class);
            TaskStackBuilder intents = TaskStackBuilder.create(context)
                    .addNextIntent(loginIntent);
            if ("".equals(settings.getString("uri", ""))) {
                intents.addNextIntentWithParentStack(settingsIntent);
            }
            intents.startActivities();
        }
    }

    private GarageOpener getGarageOpener(Context context) {
        SharedPreferences authentication = context.getSharedPreferences("authentication", 0);
        String authToken = authentication.getString("authToken", "");
        SharedPreferences settings = context.getSharedPreferences("settings", 0);
        String uri = settings.getString("uri", "");
        if ("".equals(uri) || "".equals(authToken)) {
            return null;
        }
        return new GarageOpener(uri, authToken);
    }
    
    private static void notificationStateChange(Context context, String title) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setAutoCancel(true);

        // Intent to open status activity
        Intent statusIntent = new Intent(context, StatusActivity.class);

        // Task stack will backout to homescreen
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(StatusActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(statusIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notificationStateChange later on.
        notificationManager.notify(1, notificationBuilder.build());
    }

    static class UpdateWidgetHandler extends Handler {

        private Context context;
        private int[] widgetIds;
        private boolean notify;

        UpdateWidgetHandler(Context context, int[] widgetIds, boolean notify) {
            super(Looper.getMainLooper());
            this.context = context;
            this.widgetIds = widgetIds;
            this.notify = notify;
        }

        @Override
        public void handleMessage(Message msg) {
            String state = msg.getData().getString("state");
            String error = msg.getData().getString("error");
            if (error != null && !"".equals(error)) {
                Log.w("garage.opener.widget", error + " occurred.");
            } else {
                updateButtonText(state);
            }
        }

        private void updateButtonText(String updatedState) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.garage_opener_widget);
            views.setTextViewText(R.id.widget_button_garage_opener, updatedState);
            views.setTextViewTextSize(R.id.widget_button_garage_opener, TypedValue.COMPLEX_UNIT_SP, 12);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(widgetIds, views);
            if (notify) {
                notificationStateChange(context, "Garage " + updatedState);
            }
        }
    }

    static class WidgetClickedHandler extends Handler {

        private Context context;

        WidgetClickedHandler(Context context) {
            super(Looper.getMainLooper());
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String state = msg.getData().getString("state");
            String error = msg.getData().getString("error");
            if (error != null && !"".equals(error)) {
                Log.w("garage.opener.widget", error + " occurred.");
            } else {
                notificationStateChange(context, "Garage " + Language.convertAdjectiveToOppositeVerb(state));
            }
        }
    }

}

