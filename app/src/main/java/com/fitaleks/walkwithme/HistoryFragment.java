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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fitaleks.walkwithme.adapters.HistoryTodayAdapter;
import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeDatabase;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;

/**
 * Created by alexanderkulikovskiy on 03.05.16.
 */
public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private HistoryTodayAdapter adapter;
    private static final int ALL_HISTORY_LOADER = 1;

    private static final String[] HISTORY_BY_DAY_COLUMNS = {
            WalkWithMeDatabase.HISTORY + "." + FitnessHistory.ID,
            FitnessHistory.DATE,
            "SUM("+ FitnessHistory.NUM_OF_STEPS +")",
    };

    public static final int COL_HISTORY_ID = 0;
    public static final int COL_DATE = 1;
    public static final int COL_NUMBER_OF_STEPS = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryTodayAdapter(null);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ALL_HISTORY_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(ALL_HISTORY_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                WalkWithMeProvider.History.BY_DAYS_URI,
                HISTORY_BY_DAY_COLUMNS,
                null,
                null,
                FitnessHistory.DATE + " DESC ");
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
