package com.fitaleks.walkwithme.ui.myhistory;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alexander on 20.06.16.
 */
public class MyHistoryDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String DETAIL_DATE = "DATE";
    public static final String DETAIL_TRANSITION_ANIMATION = "DTA";

    private static final int LOADER_BY_DAY = 24;

    private long date;
    private boolean transitionAnimation;

    private TextView dateTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my_history_details, container, false);
        dateTextView = (TextView) rootView.findViewById(R.id.date);

        Bundle bundle = getArguments();
        if (bundle != null) {
            date = bundle.getLong(DETAIL_DATE, 0);
            transitionAnimation = bundle.getBoolean(DETAIL_TRANSITION_ANIMATION, false);
        }
        if (transitionAnimation) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
                final AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.setSupportActionBar(toolbar);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        dateTextView.setText(String.format(Locale.getDefault(), "%d", date));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_BY_DAY, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_BY_DAY, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long beginningOfToday = calendar.getTimeInMillis();
        Log.d("MainActivty", "beToday = " + beginningOfToday);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long beginningOfTowmorrow = calendar.getTimeInMillis();
        Log.d("MainActivty", "beTomorrow = " + beginningOfTowmorrow);
        Log.d("MainActivty", "diff = " + (beginningOfTowmorrow - beginningOfToday));
        return new CursorLoader(getContext(),
                WalkWithMeProvider.History.CONTENT_URI,
                null,
                FitnessHistory.DATE + " > ? AND " + FitnessHistory.DATE + " < ?",
                new String[]{Long.toString(beginningOfToday), Long.toString(beginningOfTowmorrow)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        final StringBuilder sb = new StringBuilder();
        while (data.moveToNext()) {
            sb.append("date = ");
            sb.append(format.format(new Date(data.getLong(data.getColumnIndex(FitnessHistory.DATE)))));
            sb.append("\n");
            sb.append("steps = ");
            sb.append(data.getLong(data.getColumnIndex(FitnessHistory.NUM_OF_STEPS)));
            sb.append("\n==================\n");
        }
        dateTextView.setText(sb.toString());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dateTextView.setText("no data");
    }
}