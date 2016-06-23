package com.fitaleks.walkwithme.ui.friends;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fitaleks.walkwithme.R;
import com.fitaleks.walkwithme.adapters.MyFriendsAdapter;
import com.fitaleks.walkwithme.data.database.Friends;
import com.fitaleks.walkwithme.data.database.FriendsHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeDatabase;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.utils.ui.MarginDecoration;
import com.fitaleks.walkwithme.utils.SharedPrefUtils;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public class FriendsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FriendsContract.View {
    private static final int LOADER_FRIENDS = 0;

    private MyFriendsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FriendsContract.Presenter friendsPresenter;

    private final String[] FRIENDS_SELECTION_COLUMNS = {
            WalkWithMeDatabase.FRIENDS + "." + Friends.ID,
            Friends.FRIEND_NAME,
            Friends.PHOTO,
            Friends.GOOGLE_USER_ID,
            "SUM(" + WalkWithMeDatabase.FRIENDS_HISTORY + "." + FriendsHistory.STEPS + ")"
    };

    public static final int COL_FRIEND_ID = 0;
    public static final int COL_NAME = 1;
    public static final int COL_PHOTO = 2;
    public static final int COL_GOOGLE_USER_ID = 3;
    public static final int COL_SUM_STEPS = 4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContext().getContentResolver().delete(WalkWithMeProvider.FriendsTable.CONTENT_URI, null, null);
                friendsPresenter.reloadFriendsList();
            }
        });

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.friends_list);
        adapter = new MyFriendsAdapter(rootView.findViewById(R.id.no_data_placeholder),
                new MyFriendsAdapter.FriendsAdapterOnClickHandler() {
                    @Override
                    public void onClick(String googleId, MyFriendsAdapter.FriendViewHolder viewHolder) {
                        ((FriendsFragment.Callback) getActivity()).onItemSelected(
                                googleId,
                                viewHolder
                        );
                    }
                });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MarginDecoration(getContext()));

        return rootView;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        swipeRefreshLayout.setRefreshing(active);
    }

    @Override
    public void showNoData() {

    }

    @Override
    public void setPresenter(FriendsContract.Presenter presenter) {
        friendsPresenter = presenter;
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
        friendsPresenter.start();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                WalkWithMeProvider.FriendsTable.friendsWithSteps,
                FRIENDS_SELECTION_COLUMNS,
                Friends.GOOGLE_USER_ID + " IS NULL OR " + Friends.GOOGLE_USER_ID + " NOT LIKE ?",
                new String[]{SharedPrefUtils.getUserUid(getContext())},
                FRIENDS_SELECTION_COLUMNS[FRIENDS_SELECTION_COLUMNS.length - 1] + " DESC");
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     * <p>
     * Created by alex1101 on 28.08.14.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(String googleId, MyFriendsAdapter.FriendViewHolder viewHolder);
    }
}
