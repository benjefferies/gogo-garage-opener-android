package uk.echosoft.garage.opener;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

class ToggleGarageTask extends AsyncTask<Void, Void, String> {

    private StatusActivity statusActivity;
    private GarageOpener garageOpener;
    private TextView textView;

    ToggleGarageTask(StatusActivity statusActivity, GarageOpener garageOpener, TextView textView) {
        this.statusActivity = statusActivity;
        this.garageOpener = garageOpener;
        this.textView = textView;
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
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NotAuthenticatedException e) {
            Dialogs.unauthenticated(statusActivity).show();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String state) {
        super.onPostExecute(state);
        textView.setText(state);
    }

}
