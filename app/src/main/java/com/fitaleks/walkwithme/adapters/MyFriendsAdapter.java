package com.fitaleks.walkwithme.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.data.database.Friends;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.FriendViewHolder> {
    public Cursor cursor;

    public MyFriendsAdapter() {

    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public FriendViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_name);
        }
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.name.setText(cursor.getString(cursor.getColumnIndex(Friends.FRIEND_NAME)));
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }
}
