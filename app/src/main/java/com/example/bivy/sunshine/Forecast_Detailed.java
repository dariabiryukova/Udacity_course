package com.example.bivy.sunshine;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import static android.app.PendingIntent.getActivity;


public class Forecast_Detailed extends ActionBarActivity {

    public static final String EXTRA_FORECAST = "forecast";
    public static String forecastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast__detailed);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        TextView txtView = (TextView) findViewById(R.id.det_act_Text);
        forecastText = getIntent().getStringExtra(EXTRA_FORECAST).toString();
        txtView.setText(forecastText);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forecast__detailed, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final  String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private static final String LOG_TAG = "";
        private String mforecastString;

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.forecast_detailed_share, menu);

            MenuItem menuItem = menu.findItem(R.id.action_shareWeather);
            ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_forecast__detailed, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(EXTRA_FORECAST)) {
                mforecastString = intent.getStringExtra(EXTRA_FORECAST);
                ((TextView) rootView.findViewById(R.id.det_act_Text))
                    .setText(mforecastString);
            }

            return rootView;
        }

        private Intent createShareForecastIntent() {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mforecastString + FORECAST_SHARE_HASHTAG);

            return shareIntent;
        }
    }
}
