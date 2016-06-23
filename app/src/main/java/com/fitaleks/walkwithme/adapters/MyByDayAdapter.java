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
 * Created by alexander on 21.06.16.
 */
public class MyByDayAdapter extends RecyclerView.Adapter<MyByDayAdapter.MyByDayViewHolder> {
    class MyByDayViewHolder extends RecyclerView.ViewHolder {
        TextView stepCount;
        TextView time;
        public MyByDayViewHolder(View itemView) {
            super(itemView);
            stepCount = (TextView) itemView.findViewById(R.id.history_item_steps_count);
            time = (TextView) itemView.findViewById(R.id.history_item_date);
        }
    }

    private Cursor cursor;

    public MyByDayAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public MyByDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new MyByDayViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyByDayViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.stepCount.setText(holder.stepCount.getContext().getString(R.string.step_counter_title, cursor.getInt(HistoryFragment.COL_NUMBER_OF_STEPS)));
        final SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        final Date dateOfActivity = new Date(cursor.getLong(HistoryFragment.COL_DATE));
        holder.time.setText(format.format(dateOfActivity));
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

}
