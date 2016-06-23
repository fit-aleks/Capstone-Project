package com.fitaleks.walkwithme.ui.friends;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.adapters.FriendHistoryAdapter;
import com.fitaleks.walkwithme.data.database.Friends;
import com.fitaleks.walkwithme.data.database.FriendsHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.data.firebase.FirebaseHelper;
import com.fitaleks.walkwithme.utils.ui.CropCircleTransformation;
import com.fitaleks.walkwithme.utils.ui.MarginDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderkulikovskiy on 20.06.16.
 */
public class FriendsDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_FRIEND_DETAILS = 21;
    private static final int LOADER_FRIEND_HISTORY = 22;

    public static final String DETAIL_GOOGLE_ID = "GOOGLE_ID";
    public static final String DETAIL_TRANSITION_ANIMATION = "DTA";

    private String googleId;
    private boolean transitionAnimation;
    private ImageView photo;
    private Toolbar toolbar;

    private FriendHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            googleId = arguments.getString(DETAIL_GOOGLE_ID);
            transitionAnimation = arguments.getBoolean(DETAIL_TRANSITION_ANIMATION, false);
        }
        final View rootView = inflater.inflate(R.layout.fragment_friend_details, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);


        photo = (ImageView) rootView.findViewById(R.id.friend_avatar);
        final RecyclerView friendHistory = (RecyclerView) rootView.findViewById(R.id.list);
        friendHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendHistoryAdapter(null);
        friendHistory.setAdapter(adapter);
        friendHistory.addItemDecoration(new MarginDecoration(getContext()));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_FRIEND_DETAILS, null, this);
        getLoaderManager().initLoader(LOADER_FRIEND_HISTORY, null, this);
        loadFriendsStepHistory();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_FRIEND_DETAILS, null, this);
        getLoaderManager().restartLoader(LOADER_FRIEND_HISTORY, null, this);
    }

    private void loadFriendsStepHistory() {
        FirebaseHelper helper = new FirebaseHelper.Builder()
                .addChild(FirebaseHelper.KEY_STEPS)
                .addChild(googleId).build();
        helper.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Context context = getContext();
                if (context == null) {
                    return;
                }
                context.getContentResolver().delete(WalkWithMeProvider.FriendsHistoryTable.CONTENT_URI,
                        FriendsHistory.GOOGLE_ID + " = ?",
                        new String[]{googleId});
                List<ContentValues> history = new ArrayList<>();
                for (DataSnapshot stepRecord : dataSnapshot.getChildren()) {
                    ContentValues oneHistoryRecord = new ContentValues();
                    oneHistoryRecord.put(FriendsHistory.TIME, stepRecord.getKey());
                    final String stepCount = (String) stepRecord.getValue();
                    oneHistoryRecord.put(FriendsHistory.STEPS, stepCount);
                    oneHistoryRecord.put(FriendsHistory.GOOGLE_ID, googleId);
                    history.add(oneHistoryRecord);
                }
                if (history.size() > 0) {
                    ContentValues[] cVVector = new ContentValues[history.size()];
                    history.toArray(cVVector);
                    context.getContentResolver()
                            .bulkInsert(WalkWithMeProvider.FriendsHistoryTable.CONTENT_URI, cVVector);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_FRIEND_DETAILS) {
            return new CursorLoader(
                    getContext(),
                    WalkWithMeProvider.FriendsTable.withFriendGoogleId(googleId),
                    null,
                    null,
                    null,
                    null
            );
        } else {
            return new CursorLoader(
                    getContext(),
                    WalkWithMeProvider.FriendsHistoryTable.withId(googleId),
                    null,
                    null,
                    null,
                    null);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_FRIEND_DETAILS) {
            if (!data.moveToFirst()) {
                return;
            }
            Glide.with(getContext())
                    .load(data.getString(data.getColumnIndex(Friends.PHOTO)))
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .placeholder(R.drawable.friend_photo_placeholder)
                    .into(this.photo);
            if (transitionAnimation) {
                final AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.supportStartPostponedEnterTransition();
                activity.setSupportActionBar(toolbar);
                if (toolbar != null) {
                    activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    activity.getSupportActionBar().setTitle(data.getString(data.getColumnIndex(Friends.FRIEND_NAME)));
                }
            } else {
                if (toolbar != null) {
                    toolbar.setVisibility(View.GONE);
                }
            }
        } else {
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
