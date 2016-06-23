package com.fitaleks.walkwithme.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fitaleks.walkwithme.HistoryFragment;
import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.utils.DateUtils;

/**
 * Created by alexanderkulikovskiy on 23.04.16.
 */
public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.MyHistoryDayViewHolder> {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_HISTORY = 1;

    private Cursor cursor;
    private MyHistoryAdapterOnClickHandler clickHandler;

    public MyHistoryAdapter(Cursor cursor, MyHistoryAdapterOnClickHandler clickHandler) {
        this.cursor = cursor;
        this.clickHandler = clickHandler;
    }

    public class MyHistoryDayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView stepsCountTextView;
        public TextView dateTextView;

        public MyHistoryDayViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            stepsCountTextView = (TextView) itemView.findViewById(R.id.history_item_steps_count);
            dateTextView = (TextView) itemView.findViewById(R.id.history_item_date);
        }

        @Override
        public void onClick(View v) {
            cursor.moveToPosition(getAdapterPosition());
            clickHandler.onClick(cursor.getLong(HistoryFragment.COL_DATE), this);
        }
    }

    @Override
    public MyHistoryDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (!(parent instanceof RecyclerView )) {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }
        final int layoutId = (viewType == VIEW_TYPE_TODAY) ? R.layout.list_item_today_summary : R.layout.list_item_history;
        final View rootView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        rootView.setFocusable(true);
        return new MyHistoryDayViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyHistoryDayViewHolder holder, int position) {
        cursor.moveToPosition(position);

        holder.stepsCountTextView.setText(holder.stepsCountTextView.getContext().getString(R.string.step_counter_title, cursor.getInt(HistoryFragment.COL_NUMBER_OF_STEPS)));
        if (position != 0) {
            holder.dateTextView.setText(DateUtils.getDayName(holder.dateTextView.getContext(), cursor.getLong(HistoryFragment.COL_DATE)));
        } else {
            holder.stepsCountTextView.setText(String.format("%s, %s",
                    holder.stepsCountTextView.getContext().getString(R.string.date_today),
                    holder.stepsCountTextView.getContext().getString(R.string.step_counter_title, cursor.getInt(HistoryFragment.COL_NUMBER_OF_STEPS))));
        }
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

    public interface MyHistoryAdapterOnClickHandler {
        void onClick(long date, MyHistoryDayViewHolder viewHolder);
    }
}
