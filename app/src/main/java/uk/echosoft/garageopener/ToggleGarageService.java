package uk.echosoft.garageopener;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
                        updateGarageState(intent, garageOpener, originalState);
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
            throw new RuntimeException(e);
        } catch (NotAuthenticatedException e) {
            requireLogin();
        }
    }

    private void requireLogin() {
        Intent loginIntent = new Intent(ToggleGarageService.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
    }

    private void updateGarageState(Intent intent, GarageOpener garageOpener, String originalState) throws IOException, NotAuthenticatedException, InterruptedException {
        int retries = 0;
        while (Objects.equals(originalState, garageOpener.getGarageState()) && retries < 15) {
            retries++;
            TimeUnit.SECONDS.sleep(1);
        }
        String state = garageOpener.getGarageState();
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.garage_opener_widget);
        views.setTextViewText(R.id.widget_button_garage_opener, state);
        views.setTextViewTextSize(R.id.widget_button_garage_opener, TypedValue.COMPLEX_UNIT_SP, 12);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        appWidgetManager.updateAppWidget(widgetIds, views);
    }
}
