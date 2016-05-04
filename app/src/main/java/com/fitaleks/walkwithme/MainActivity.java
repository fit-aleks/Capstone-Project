package com.fitaleks.walkwithme;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // State of application, used to register for sensors when app is restored
    private static final int STATE_OTHER = 0;
    public static final int STATE_COUNTER = 1;

    // Number of events to keep in queue and display on card
    private static final int EVENT_QUEUE_LENGTH = 10;

    private float[] mEventDelays = new float[EVENT_QUEUE_LENGTH];

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;


    // number of events in event list
    private int mEventLength = 0;
    // pointer to next entry in sensor event list
    private int mEventData = 0;
    // State of the app (STATE_OTHER, STATE_COUNTER or STATE_DETECTOR)
    private int mState = STATE_OTHER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        final TextView textView = (TextView) findViewById(R.id.main_text);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }


        if (!isServiceRunning()) {
            final Intent startStepService = new Intent(this, StepCounterService.class);
            startService(startStepService);
        }
//        textView.setText(String.format(Locale.getDefault(), "Инфа из SharedPreferences, куда должен был бы положить их сервис. Шагов за сегодня: %d\nСервис запущен: %b", mPreviousCounterSteps, isServiceRunning()));

    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (StepCounterService.class.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.removeDrawerListener(toggle);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_me) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new TodayFragment())
                    .commit();
            // Show today
        } else if (id == R.id.nav_history) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new HistoryFragment())
                    .commit();
            // Show all history
        } else if (id == R.id.nav_friends) {
            // Show today's friends
        } else if (id == R.id.nav_manage) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new SettingsFragment())
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
