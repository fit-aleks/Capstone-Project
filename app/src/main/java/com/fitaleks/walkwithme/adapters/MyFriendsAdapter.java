package com.fitaleks.walkwithme.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.data.database.Friends;
import com.fitaleks.walkwithme.utils.CropCircleTransformation;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.FriendViewHolder> {
    public Cursor cursor;

    public MyFriendsAdapter() {

    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView photo;
        public FriendViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_name);
            photo = (ImageView) itemView.findViewById(R.id.list_item_photo);
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
        Glide.with(holder.photo.getContext())
                .load(cursor.getString(cursor.getColumnIndex(Friends.PHOTO)))
                .bitmapTransform(new CropCircleTransformation(holder.photo.getContext()))
                .placeholder(R.drawable.friend_photo_placeholder)
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }
}
