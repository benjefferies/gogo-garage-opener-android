package uk.echosoft.garage.opener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

class LoadStateTask extends AsyncTask<Void, Void, String> {

    private final Handler handler;
    private final GarageOpener garageOpener;

    LoadStateTask(GarageOpener garageOpener, Handler handler) {
        this.garageOpener = garageOpener;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return garageOpener.getGarageState();
        } catch (IOException | NotAuthenticatedException e) {
            Bundle bundle = new Bundle();
            bundle.putString("error", e.getClass().getSimpleName());
            Message message = Message.obtain(handler);
            message.setData(bundle);
            message.sendToTarget();
            Log.w("garage.opener.load", "Could not get garage state", e);
        }
        return "";
    }

    @Override
    protected void onPostExecute(String state) {
        super.onPostExecute(state);
        Bundle bundle = new Bundle();
        bundle.putString("state", state);
        Message message = Message.obtain(handler);
        message.setData(bundle);
        message.sendToTarget();
    }
}
