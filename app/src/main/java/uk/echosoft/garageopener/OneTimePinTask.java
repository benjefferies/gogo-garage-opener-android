package uk.echosoft.garageopener;

import android.os.AsyncTask;

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
            throw new RuntimeException(e);
        }
        return "";
    }

    @Override
    protected void onPostExecute(final String oneTimePin) {
        super.onPostExecute(oneTimePin);
        Dialogs.oneTimePin(activity, oneTimePin);
    }

}
