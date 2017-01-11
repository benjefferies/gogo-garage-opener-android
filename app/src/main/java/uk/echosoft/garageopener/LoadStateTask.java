package uk.echosoft.garageopener;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;

class LoadStateTask extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private GarageOpener garageOpener;
    private TextView textView;

    public LoadStateTask(Activity activity, GarageOpener garageOpener, TextView textView) {
        this.activity = activity;
        this.garageOpener = garageOpener;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return garageOpener.getGarageState();
        } catch (IOException e) {
            e.printStackTrace();
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
    }
}
