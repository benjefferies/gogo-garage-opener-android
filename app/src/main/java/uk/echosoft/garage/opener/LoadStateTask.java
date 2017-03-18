package uk.echosoft.garage.opener;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

class LoadStateTask extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private GarageOpener garageOpener;
    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;

    LoadStateTask(Activity activity, GarageOpener garageOpener, TextView textView, SwipeRefreshLayout swipeRefreshLayout) {
        this.activity = activity;
        this.garageOpener = garageOpener;
        this.textView = textView;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return garageOpener.getGarageState();
        } catch (IOException e) {
            Log.w("toggle.load", "Could not get garage state", e);
        } catch (NotAuthenticatedException e) {
            Dialogs.unauthenticated(activity).show();
            activity.finish();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String state) {
        super.onPostExecute(state);
        textView.setText(state);
        swipeRefreshLayout.setRefreshing(false);
    }
}
