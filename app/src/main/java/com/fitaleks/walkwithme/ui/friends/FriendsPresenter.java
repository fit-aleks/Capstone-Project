package com.fitaleks.walkwithme.ui.friends;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.fitaleks.walkwithme.WalkWithMeApplication;
import com.fitaleks.walkwithme.data.database.Friends;
import com.fitaleks.walkwithme.data.database.FriendsHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.data.firebase.FirebaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alexander on 22.06.16.
 */
public class FriendsPresenter implements FriendsContract.Presenter {

    private final FriendsContract.View friendsView;

    public FriendsPresenter(@NonNull FriendsContract.View friendsView) {
        this.friendsView = friendsView;
        this.friendsView.setPresenter(this);
    }

    @Override
    public void reloadFriendsList() {
        WalkWithMeApplication.getApplication().getContentResolver().delete(WalkWithMeProvider.FriendsTable.CONTENT_URI, null, null);
        loadFriends();
    }

    @Override
    public void start() {
        loadFriends();
    }

    private void loadFriends() {
        final FirebaseHelper helper = new FirebaseHelper.Builder()
                .addChild(FirebaseHelper.KEY_USERS)
                .build();
        helper.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<ContentValues> users = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final HashMap<String, String> values = (HashMap<String, String>) child.getValue();

                    final ContentValues contentValues = new ContentValues();
                    contentValues.put(Friends.FRIEND_NAME, values.get(Friends.FRIEND_NAME));
                    contentValues.put(Friends.PHOTO, values.get(Friends.PHOTO));
                    contentValues.put(Friends.GOOGLE_USER_ID, values.get(Friends.GOOGLE_USER_ID));
                    users.add(contentValues);

                    loadFriendStepHistory(values.get(Friends.GOOGLE_USER_ID));
                }
                if (users.size() > 0) {
                    ContentValues[] cVVector = new ContentValues[users.size()];
                    users.toArray(cVVector);
                    WalkWithMeApplication.getApplication()
                            .getContentResolver()
                            .bulkInsert(WalkWithMeProvider.FriendsTable.CONTENT_URI, cVVector);
                }
                friendsView.setLoadingIndicator(false);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void loadFriendStepHistory(@NonNull final String googleId) {
        FirebaseHelper helper = new FirebaseHelper.Builder()
                .addChild(FirebaseHelper.KEY_STEPS)
                .addChild(googleId).build();
        helper.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Context context = WalkWithMeApplication.getApplication();
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
                    final String stepCount = (String)stepRecord.getValue();
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

}
