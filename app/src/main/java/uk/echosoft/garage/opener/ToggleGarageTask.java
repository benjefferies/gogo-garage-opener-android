package uk.echosoft.garage.opener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

class ToggleGarageTask extends AsyncTask<Void, Void, String> {

    private final Handler handler;
    private final GarageOpener garageOpener;

    ToggleGarageTask(Handler handler, GarageOpener garageOpener) {
        this.handler = handler;
        this.garageOpener = garageOpener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            final String originalState = garageOpener.getGarageState();
            garageOpener.toggleGarageDoor();
            int retries = 0;
            while (Objects.equals(originalState, garageOpener.getGarageState()) && retries < 15 && !isCancelled()) {
                retries++;
                TimeUnit.SECONDS.sleep(1);
            }
            return garageOpener.getGarageState();
        } catch (IOException | InterruptedException | NotAuthenticatedException e) {
            Bundle bundle = new Bundle();
            bundle.putString("error", e.getClass().getSimpleName());
            Message message = Message.obtain(handler);
            message.setData(bundle);
            message.sendToTarget();
            Log.w("garage.opener.toggle", "Unable to get garage state", e);
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
