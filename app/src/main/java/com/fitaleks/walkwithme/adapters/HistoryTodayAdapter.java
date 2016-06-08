package com.fitaleks.walkwithme.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fitaleks.walkwithme.HistoryFragment;
import com.fitaleks.walkwithme.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alexanderkulikovskiy on 23.04.16.
 */
public class HistoryTodayAdapter extends RecyclerView.Adapter<HistoryTodayAdapter.TodayViewHolder> {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_HISTORY = 1;

    private Cursor cursor;
    public HistoryTodayAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    class TodayViewHolder extends RecyclerView.ViewHolder {
        private TextView stepsCountTextView;
        private TextView dateTextView;
        public TodayViewHolder(View itemView) {
            super(itemView);
            stepsCountTextView = (TextView) itemView.findViewById(R.id.history_item_steps_count);
            dateTextView = (TextView) itemView.findViewById(R.id.history_item_date);
        }

    }

    @Override
    public TodayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (!(parent instanceof RecyclerView )) {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }
        final int layoutId = (viewType == VIEW_TYPE_TODAY) ? R.layout.list_item_today_summary : R.layout.history_item;
        final View rootView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        rootView.setFocusable(true);
        return new TodayViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(TodayViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.stepsCountTextView.setText(holder.stepsCountTextView.getContext().getString(R.string.step_counter_title, cursor.getInt(HistoryFragment.COL_NUMBER_OF_STEPS)));
        final SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        final Date dateOfActivity = new Date(cursor.getLong(HistoryFragment.COL_DATE));
        holder.dateTextView.setText(format.format(dateOfActivity));
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_HISTORY;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }
}
