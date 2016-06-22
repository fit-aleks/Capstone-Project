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
import com.fitaleks.walkwithme.ui.friends.FriendsFragment;
import com.fitaleks.walkwithme.utils.CropCircleTransformation;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.FriendViewHolder> {
    public Cursor cursor;
    private FriendsAdapterOnClickHandler onClickHandler;
    private final View emptyView;


    public MyFriendsAdapter(View emptyView,FriendsAdapterOnClickHandler clickHandler) {
        this.emptyView = emptyView;
        this.onClickHandler = clickHandler;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView stepsCount;
        ImageView photo;

        public FriendViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.list_item_name);
            photo = (ImageView) itemView.findViewById(R.id.list_item_photo);
            stepsCount = (TextView) itemView.findViewById(R.id.list_item_steps_count);
        }

        @Override
        public void onClick(View v) {
            cursor.moveToPosition(getAdapterPosition());
            final String googleId = cursor.getString(FriendsFragment.COL_GOOGLE_USER_ID);
            onClickHandler.onClick(googleId, this);
        }
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
        if (cursor == null || cursor.getCount() == 0) {
            this.emptyView.setVisibility(View.VISIBLE);
        } else {
            this.emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.name.setText(cursor.getString(FriendsFragment.COL_NAME));
        holder.stepsCount.setText(holder.stepsCount.getContext().getString(R.string.step_counter_title, cursor.getLong(FriendsFragment.COL_SUM_STEPS)));
        Glide.with(holder.photo.getContext())
                .load(cursor.getString(FriendsFragment.COL_PHOTO))
                .bitmapTransform(new CropCircleTransformation(holder.photo.getContext()))
                .placeholder(R.drawable.friend_photo_placeholder)
                .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public interface FriendsAdapterOnClickHandler {
        void onClick(String googleId, FriendViewHolder viewHolder);
    }
}
