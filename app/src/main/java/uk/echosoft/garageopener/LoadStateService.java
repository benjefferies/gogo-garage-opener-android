package uk.echosoft.garageopener;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.io.IOException;

public class LoadStateService extends IntentService {

    public LoadStateService() {
        super("load.garage.state");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences authentication = getSharedPreferences("authentication", 0);
        String authToken = authentication.getString("authToken", "");
        SharedPreferences settings = getSharedPreferences("settings", 0);
        String uri = settings.getString("uri", "");
        if ("".equals(uri) || "".equals(authToken)) {
            return;
        }
        GarageOpener garageOpener = new GarageOpener(uri, authToken);
        try {
            String state = garageOpener.getGarageState();
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.garage_opener_widget);
            views.setTextViewText(R.id.widget_button_garage_opener, state);
            views.setTextViewTextSize(R.id.widget_button_garage_opener, TypedValue.COMPLEX_UNIT_SP, 12);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            appWidgetManager.updateAppWidget(widgetIds, views);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NotAuthenticatedException e) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        }

    }
}
