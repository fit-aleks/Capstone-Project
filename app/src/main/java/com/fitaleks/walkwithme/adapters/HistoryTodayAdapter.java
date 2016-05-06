package com.fitaleks.walkwithme.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.data.database.FitnessHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alexanderkulikovskiy on 23.04.16.
 */
public class HistoryTodayAdapter extends RecyclerView.Adapter<HistoryTodayAdapter.TodayViewHolder> {

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
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.history_item, parent, false);
        return new TodayViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(TodayViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.stepsCountTextView.setText(holder.stepsCountTextView.getContext().getString(R.string.step_counter_title, cursor.getInt(cursor.getColumnIndex(FitnessHistory.NUM_OF_STEPS))));
        final SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss", Locale.getDefault());
        final Date dateOfActivity = new Date(cursor.getLong(cursor.getColumnIndex(FitnessHistory.DATE)));
        holder.dateTextView.setText(format.format(dateOfActivity));
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
