package uk.echosoft.garage.opener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

class OneTimePinTask extends AsyncTask<Void, Void, String> {

    private GarageOpener garageOpener;
    private Handler handler;

    OneTimePinTask(Handler handler, GarageOpener garageOpener) {
        this.handler = handler;
        this.garageOpener = garageOpener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return garageOpener.getOneTimePin();
        } catch (NotAuthenticatedException | IOException e) {
            Bundle bundle = new Bundle();
            bundle.putString("error", e.getClass().getSimpleName());
            Message message = Message.obtain(handler);
            message.setData(bundle);
            message.sendToTarget();
            Log.w("toggle.one.time.pin", "Could not get one time pin", e);
        }
        return "";
    }

    @Override
    protected void onPostExecute(final String oneTimePin) {
        super.onPostExecute(oneTimePin);
        if (!"".equals(oneTimePin)) {
            Bundle bundle = new Bundle();
            bundle.putString("one-time-pin", oneTimePin);
            Message message = Message.obtain(handler);
            message.setData(bundle);
            message.sendToTarget();
        }
    }

}
