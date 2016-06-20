package com.fitaleks.walkwithme.ui.myhistory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.ui.friends.FriendsDetailsFragment;

/**
 * Created by alexander on 20.06.16.
 */
public class MyHistoryDetailsActivity extends AppCompatActivity {
    public static final String KEY_DATE = "date";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history_details);

        if (savedInstanceState == null) {
            final long date = getIntent().getLongExtra(KEY_DATE, 0);

            final Bundle bundle = new Bundle();
            bundle.putLong(MyHistoryDetailsFragment.DETAIL_DATE, date);
            bundle.putBoolean(MyHistoryDetailsFragment.DETAIL_TRANSITION_ANIMATION, true);

            final MyHistoryDetailsFragment historyDetailsFragment = new MyHistoryDetailsFragment();
            historyDetailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, historyDetailsFragment)
                    .commit();
        }
    }
}
