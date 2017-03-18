package uk.echosoft.garage.opener;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

class OneTimePinTask extends AsyncTask<Void, Void, String> {

    private GarageOpener garageOpener;
    private StatusActivity activity;

    public OneTimePinTask(StatusActivity activity, GarageOpener garageOpener) {
        this.activity = activity;
        this.garageOpener = garageOpener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return garageOpener.getOneTimePin();
        } catch (NotAuthenticatedException e) {
            Dialogs.unauthenticated(activity).show();
        } catch (IOException e) {
            Log.w("toggle.one.time.pin", "Could not get one time pin", e);
        }
        return "";
    }

    @Override
    protected void onPostExecute(final String oneTimePin) {
        super.onPostExecute(oneTimePin);
        Dialogs.oneTimePin(activity, oneTimePin);
    }

}
