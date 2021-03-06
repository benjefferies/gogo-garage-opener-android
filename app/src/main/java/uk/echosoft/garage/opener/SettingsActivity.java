package uk.echosoft.garage.opener;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private EditText garageOpenerTextField;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(uk.echosoft.garage.opener.R.menu.menu_settings, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(uk.echosoft.garage.opener.R.layout.activity_settings);
        garageOpenerTextField = (EditText) findViewById(uk.echosoft.garage.opener.R.id.garage_opener_url);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        String uri = settings.getString("uri", "");
        garageOpenerTextField.setText(uri);
        Toolbar toolbar = (Toolbar) findViewById(uk.echosoft.garage.opener.R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case uk.echosoft.garage.opener.R.id.action_save:
                String garageUrl = garageOpenerTextField.getText().toString();
                if ("".equals(garageUrl) || URLUtil.isValidUrl(garageUrl)) {
                    save();
                    return true;
                } else {
                    garageOpenerTextField.setError("malformed url, must start with http:// or https://");
                }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void save() {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        settings.edit().putString("uri", garageOpenerTextField.getText().toString()).apply();
        finish();
    }
}
