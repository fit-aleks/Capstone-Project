package com.fitaleks.walkwithme.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fitaleks.walkwithme.HistoryFragment;
import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.data.database.FriendsHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by alexander on 20.06.16.
 */
public class FriendHistoryAdapter extends RecyclerView.Adapter<FriendHistoryAdapter.FriendHistoryViewHolder> {

    class FriendHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView stepsCount;
        TextView date;
        public FriendHistoryViewHolder(View itemView) {
            super(itemView);
            stepsCount = (TextView) itemView.findViewById(R.id.history_item_steps_count);
            date = (TextView) itemView.findViewById(R.id.history_item_date);
        }
    }

    private Cursor cursor;

    public FriendHistoryAdapter(Cursor dataCursor) {
        this.cursor = dataCursor;
    }

    public void swapCursor(Cursor newData) {
        this.cursor = newData;
        notifyDataSetChanged();
    }

    @Override
    public FriendHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new FriendHistoryViewHolder(inflater.inflate(R.layout.list_item_friend_history, parent, false));
    }

    @Override
    public void onBindViewHolder(FriendHistoryViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.stepsCount.setText(holder.stepsCount.getContext().getString(R.string.step_counter_title,
                cursor.getLong(cursor.getColumnIndex(FriendsHistory.STEPS))));
        final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM, HH:mm", Locale.getDefault());
        final Date dateOfActivity = new Date(cursor.getLong(cursor.getColumnIndex(FriendsHistory.TIME)));
        holder.date.setText(format.format(dateOfActivity));
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }
}
