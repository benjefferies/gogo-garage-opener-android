package uk.echosoft.garage.opener;

import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class StatusActivity extends AppCompatActivity {

    private Button stateButton;
    private AsyncTask<Void, Void, String> toggleGarageTask;
    private GarageOpener garageOpener;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences authentication = getSharedPreferences("authentication", 0);
        String authToken = authentication.getString("authToken", "");
        SharedPreferences settings = getSharedPreferences("settings", 0);
        String uri = settings.getString("uri", "");
        this.garageOpener = new GarageOpener(uri, authToken);
        setContentView(uk.echosoft.garage.opener.R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(uk.echosoft.garage.opener.R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(uk.echosoft.garage.opener.R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case uk.echosoft.garage.opener.R.id.action_sign_out:
                Dialogs.signOut(this);
                return super.onOptionsItemSelected(item);
            case uk.echosoft.garage.opener.R.id.action_one_time_pin:
                new OneTimePinTask(this, garageOpener).execute();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.stateButton = (Button) findViewById(uk.echosoft.garage.opener.R.id.action_garage_state);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(uk.echosoft.garage.opener.R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                            refreshState();
                    }
                }
        );
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleGarageTask != null) { toggleGarageTask.cancel(false); }
                toggleGarageTask = new ToggleGarageTask(StatusActivity.this, garageOpener, stateButton).execute();
            }
        });
        refreshState();
    }

    private AsyncTask<Void, Void, String> refreshState() {
        LoadStateTask getStateTask = new LoadStateTask(this, garageOpener, stateButton, swipeRefreshLayout);
        return getStateTask.execute();
    }


}
