package uk.echosoft.garage.opener;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
            RemoteViews views = new RemoteViews(getPackageName(), uk.echosoft.garage.opener.R.layout.garage_opener_widget);
            views.setTextViewText(uk.echosoft.garage.opener.R.id.widget_button_garage_opener, state);
            views.setTextViewTextSize(uk.echosoft.garage.opener.R.id.widget_button_garage_opener, TypedValue.COMPLEX_UNIT_SP, 12);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            appWidgetManager.updateAppWidget(widgetIds, views);
        } catch (IOException e) {
            Log.w("toggle.garage", "Could not load garage state", e);
        } catch (NotAuthenticatedException e) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        }

    }
}
