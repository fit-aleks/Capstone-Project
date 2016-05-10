package com.fitaleks.walkwithme;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.fitaleks.walkwithme.adapters.MyFriendsAdapter;
import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.Friends;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.data.firebase.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public class FriendsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_FRIENDS = 0;

    private MyFriendsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.friends_list);
        adapter = new MyFriendsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFriends();
        return rootView;
    }

    private void loadFriends() {
        final FirebaseHelper helper = new FirebaseHelper.Builder()
                .addChild("users")
                .build();
        helper.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<ContentValues> users = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Friends.FRIEND_NAME, child.getKey());
                    users.add(contentValues);
                }
                if (users.size() > 0) {
                    ContentValues []cVVector = new ContentValues[users.size()];
                    users.toArray(cVVector);
                    WalkWithMeApplication.getApplication()
                            .getContentResolver()
                            .bulkInsert(WalkWithMeProvider.FriendsTable.CONTENT_URI, cVVector);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_FRIENDS, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_FRIENDS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                WalkWithMeProvider.FriendsTable.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }
}
