package com.fitaleks.walkwithme.ui.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.fitaleks.walkwithme.R;

/**
 * Created by alexanderkulikovskiy on 20.06.16.
 */
public class FriendsDetailsActivity extends AppCompatActivity {
    public static final String KEY_GOOGLE_ID = "google_id";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);

        if (savedInstanceState == null) {
            final Bundle bundle = new Bundle();
            bundle.putString(FriendsDetailsFragment.DETAIL_URI, getIntent().getStringExtra(KEY_GOOGLE_ID));
            bundle.putBoolean(FriendsDetailsFragment.DETAIL_TRANSITION_ANIMATION, true);

            final FriendsDetailsFragment detailsFragment = new FriendsDetailsFragment();
            detailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.friend_detail_container, detailsFragment)
                    .commit();

            // Being here means we are in animation mode
            supportPostponeEnterTransition();
        }
    }
}
