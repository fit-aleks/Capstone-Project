package com.fitaleks.walkwithme;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fitaleks.walkwithme.adapters.MyHistoryAdapter;
import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;

import java.util.Calendar;

/**
 * Fragment to display user's today's detailed physical activity
 *
 * Created by alexanderkulikovskiy on 03.05.16.
 */
public class TodayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private int LOADER_TODAY_HISTORY = 0;

    private MyHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.content_main, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.today_history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyHistoryAdapter(null);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_TODAY_HISTORY, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_TODAY_HISTORY, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Calendar calendar = Calendar.getInstance();
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
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
