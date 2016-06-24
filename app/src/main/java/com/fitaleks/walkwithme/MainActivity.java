package com.fitaleks.walkwithme;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.fitaleks.walkwithme.adapters.MyFriendsAdapter;
import com.fitaleks.walkwithme.adapters.MyHistoryAdapter;
import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.data.firebase.FirebaseHelper;
import com.fitaleks.walkwithme.ui.friends.FriendsDetailsActivity;
import com.fitaleks.walkwithme.ui.friends.FriendsDetailsFragment;
import com.fitaleks.walkwithme.ui.friends.FriendsFragment;
import com.fitaleks.walkwithme.ui.friends.FriendsPresenter;
import com.fitaleks.walkwithme.ui.myhistory.MyHistoryDetailsActivity;
import com.fitaleks.walkwithme.ui.myhistory.MyHistoryDetailsFragment;
import com.fitaleks.walkwithme.utils.ui.CropCircleTransformation;
import com.fitaleks.walkwithme.utils.DeviceUtils;
import com.fitaleks.walkwithme.utils.SharedPrefUtils;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        FriendsFragment.Callback,
        HistoryFragment.Callback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String STATE_SELECTED_ENTITY_ID = "selected_entuty";
    private long stateSelectedId = -1;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private TextView navNameTextView;
    private ImageView navImageView;

    private boolean mTwoPane;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient googleApiClient;
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;
    // request code for requesting permission
    private static final int RC_GET_ACCOUNTS = 42;

    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;

    /* Track whether the sign-in button has been clicked so that we know to resolve all issues preventing sign-in
     * without waiting. */
    private boolean mGoogleLoginClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can resolve them when the user clicks
     * sign-in. */
    private ConnectionResult mGoogleConnectionResult;

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = findViewById(R.id.detail_container) != null;

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mAuthProgressDialog = new ProgressDialog(this);
        /* Setup the Google API object to allow Google+ logins */
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().performIdentifierAction(R.id.nav_history, 0);
            final View navigationHeader = navigationView.getHeaderView(0);
            if (navigationHeader != null) {
                navigationHeader.setOnClickListener(onNavHeaderClickListener);
            }
            navNameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
            navImageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_image_view);
            final String userName = SharedPrefUtils.getUserName(this);
            final String userPhoto = SharedPrefUtils.getUserPhoto(this);
            if (!userName.equals("")) {
                navNameTextView.setText(userName);
            }
            if (!userPhoto.equals("")) {
                Glide.with(this)
                        .load(userPhoto)
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(navImageView);
            }
        }
        if (savedInstanceState != null) {
            stateSelectedId = savedInstanceState.getLong(STATE_SELECTED_ENTITY_ID, R.id.nav_history);
            openNavigationFragment();
        }

        if (DeviceUtils.isKitkatWithStepSensor(this) && !isServiceRunning()) {
            final Intent startStepService = new Intent(this, StepCounterService.class);
            startService(startStepService);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_SELECTED_ENTITY_ID, stateSelectedId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_GET_ACCOUNTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loginToGoogle();
            } else {
                Toast.makeText(this, R.string.auth_permission_needed_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private View.OnClickListener onNavHeaderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = checkSelfPermission(Manifest.permission.GET_ACCOUNTS);
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    loginToGoogle();
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.GET_ACCOUNTS)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.dialog_permission_auth_title)
                                .setMessage(R.string.dialog_permission_auth_message)
                                .setPositiveButton(R.string.dialog_permission_auth_positive, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, RC_GET_ACCOUNTS);
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, RC_GET_ACCOUNTS);
                    }

                }
            } else {
                loginToGoogle();
            }
        }
    };

    private void loginToGoogle() {
        mGoogleLoginClicked = true;
        if (!googleApiClient.isConnecting()) {
            if (mGoogleConnectionResult != null) {
                resolveSignInError();
            } else if (googleApiClient.isConnected()) {
                getGoogleOAuthTokenAndLogin();
            } else {
                // connect API now
                Log.d(TAG, "Trying to connect to Google API");
                googleApiClient.connect();
            }
        }
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        stateSelectedId = item.getItemId();
        openNavigationFragment();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openNavigationFragment() {
        Fragment newFragment = null;
        if (stateSelectedId == R.id.nav_history) {
            newFragment = new HistoryFragment(); // Show all history
        } else if (stateSelectedId == R.id.nav_friends) {
            newFragment = new FriendsFragment(); // Show today's friends
            new FriendsPresenter((FriendsFragment) newFragment);
        } else if (stateSelectedId == R.id.nav_manage) {
            newFragment = new SettingsFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, newFragment)
                .commit();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("MainActivity", "onConnected");
        getGoogleOAuthTokenAndLogin();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!mGoogleIntentInProgress) {
            /* Store the ConnectionResult so that we can use it later when the user clicks on the Google+ login button */
            mGoogleConnectionResult = connectionResult;

            if (mGoogleLoginClicked) {
                /* The user has already clicked login so we attempt to resolve all errors until the user is signed in,
                 * or they cancel. */
                resolveSignInError();
            } else {
                Log.e(TAG, connectionResult.toString());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_LOGIN) {
            if (resultCode != RESULT_OK) {
                mGoogleLoginClicked = false;
            }
            mGoogleIntentInProgress = false;
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mGoogleConnectionResult.hasResolution()) {
            try {
                mGoogleIntentInProgress = true;
                mGoogleConnectionResult.startResolutionForResult(this, RC_GOOGLE_LOGIN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mGoogleIntentInProgress = false;
                googleApiClient.connect();
            }
        }
    }


    private void getGoogleOAuthTokenAndLogin() {
        mAuthProgressDialog.show();
        // Get OAuth token in Background
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    String scope = String.format("oauth2:%s", Scopes.PLUS_LOGIN);
                    token = GoogleAuthUtil.getToken(MainActivity.this, Plus.AccountApi.getAccountName(googleApiClient), scope);
                } catch (IOException transientEx) {
                    // Network or server error
                    Log.e(TAG, "Error authenticating with Google: " + transientEx);
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!mGoogleIntentInProgress) {
                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, RC_GOOGLE_LOGIN);
                    }
                } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                    Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
                    errorMessage = "Error authenticating with Google: " + authEx.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                mGoogleLoginClicked = false;
                if (token != null) {
                    /* Successfully got OAuth token, now login with Google */
                    final Firebase firebaseRef = new FirebaseHelper.Builder().build().getFirebase();
                    firebaseRef.authWithOAuthToken("google", token, new AuthResultHandler("google"));
                } else if (errorMessage != null) {
                    mAuthProgressDialog.dismiss();
                    showErrorDialog(errorMessage);
                }
            }
        };
        task.execute();
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser() {
        navNameTextView.setText(SharedPrefUtils.getUserName(this));
        Glide.with(this)
                .load(SharedPrefUtils.getUserPhoto(this))
                .bitmapTransform(new CropCircleTransformation(this))
                .into(navImageView);
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            Log.i(TAG, provider + " auth successful");
            if (authData == null) {
                return;
            }
            SharedPrefUtils.setUserName(MainActivity.this, authData.getProviderData().get("displayName").toString());
            SharedPrefUtils.setUserUid(MainActivity.this, authData.getUid());
            SharedPrefUtils.setUserPhoto(MainActivity.this, authData.getProviderData().get("profileImageURL").toString());
            setAuthenticatedUser();
            startService(new Intent(MainActivity.this, SyncDataService.class));
            mAuthProgressDialog.dismiss();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.dismiss();
            showErrorDialog(firebaseError.toString());
        }
    }

    @Override
    public void onItemSelected(String googleId, MyFriendsAdapter.FriendViewHolder viewHolder) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putString(FriendsDetailsFragment.DETAIL_GOOGLE_ID, googleId);
            FriendsDetailsFragment detailsFragment = new FriendsDetailsFragment();
            detailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, detailsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, FriendsDetailsActivity.class);
            intent.putExtra(FriendsDetailsActivity.KEY_GOOGLE_ID, googleId);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            new Pair<View, String>(viewHolder.photo, getString(R.string.friend_photo_transition_name)));
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        }
    }

    @Override
    public void onItemSelected(long date, MyHistoryAdapter.MyHistoryDayViewHolder viewHolder) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putLong(MyHistoryDetailsFragment.DETAIL_DATE, date);
            MyHistoryDetailsFragment detailsFragment = new MyHistoryDetailsFragment();
            detailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, detailsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MyHistoryDetailsActivity.class);
            intent.putExtra(MyHistoryDetailsActivity.KEY_DATE, date);
            ActivityCompat.startActivity(this, intent, null);
        }
    }
}
