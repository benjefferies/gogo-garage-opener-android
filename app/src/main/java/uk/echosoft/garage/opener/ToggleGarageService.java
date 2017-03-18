package uk.echosoft.garage.opener;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static uk.echosoft.garage.opener.Language.convertAdjectiveToOppositeVerb;

public class ToggleGarageService extends IntentService {

    public ToggleGarageService() {
        super("toggleGarage");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        SharedPreferences authentication = getSharedPreferences("authentication", 0);
        String authToken = authentication.getString("authToken", "");
        SharedPreferences settings = getSharedPreferences("settings", 0);
        String uri = settings.getString("uri", "");
        if ("".equals(uri) || "".equals(authToken)) {
            return;
        }
        final GarageOpener garageOpener = new GarageOpener(uri, authToken);
        try {
            final String originalState = garageOpener.getGarageState();
            garageOpener.toggleGarageDoor();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        notificationStateChange("Garage " + convertAdjectiveToOppositeVerb(originalState));
                        String updatedState = getUpdatedGarageState(garageOpener, originalState);
                        updateButtonText(updatedState, intent);
                        notificationStateChange("Garage " + updatedState);
                    } catch (IOException e) {
                        Log.w("toggle.garage", "Could not update garage state", e);
                    } catch (NotAuthenticatedException e) {
                        requireLogin();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        } catch (IOException e) {
            Log.w("toggle.garage", "Could not toggle garage state", e);
        } catch (NotAuthenticatedException e) {
            requireLogin();
        }
    }

    private void updateButtonText(String updatedState, Intent intent) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.garage_opener_widget);
        views.setTextViewText(R.id.widget_button_garage_opener, updatedState);
        views.setTextViewTextSize(R.id.widget_button_garage_opener, TypedValue.COMPLEX_UNIT_SP, 12);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ToggleGarageService.this);
        int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        appWidgetManager.updateAppWidget(widgetIds, views);
    }

    private void notificationStateChange(String title) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true);

        // Intent to open status activity
        Intent statusIntent = new Intent(this, StatusActivity.class);

        // Task stack will backout to homescreen
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(StatusActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(statusIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notificationStateChange later on.
        notificationManager.notify(1, notificationBuilder.build());
    }

    private void requireLogin() {
        Intent loginIntent = new Intent(ToggleGarageService.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
    }

    private String getUpdatedGarageState(GarageOpener garageOpener, String originalState) throws IOException, NotAuthenticatedException, InterruptedException {
        int retries = 0;
        while (Objects.equals(originalState, garageOpener.getGarageState()) && retries < 15) {
            retries++;
            TimeUnit.SECONDS.sleep(1);
        }
        return garageOpener.getGarageState();
    }
}
