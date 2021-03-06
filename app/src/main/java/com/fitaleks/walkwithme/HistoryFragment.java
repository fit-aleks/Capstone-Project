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

import com.fitaleks.walkwithme.adapters.MyHistoryAdapter;
import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeDatabase;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.utils.ui.DividerItemDecoration;

/**
 * Created by alexanderkulikovskiy on 03.05.16.
 */
public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private MyHistoryAdapter adapter;
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
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new MyHistoryAdapter(null, new MyHistoryAdapter.MyHistoryAdapterOnClickHandler() {
            @Override
            public void onClick(long date, MyHistoryAdapter.MyHistoryDayViewHolder viewHolder) {
                ((HistoryFragment.Callback)getActivity()).onItemSelected(date, viewHolder);
            }
        });
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

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     *
     * Created by alex1101 on 28.08.14.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(long date, MyHistoryAdapter.MyHistoryDayViewHolder viewHolder);
    }
}
