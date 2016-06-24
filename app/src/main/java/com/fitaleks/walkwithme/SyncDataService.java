package com.fitaleks.walkwithme;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.fitaleks.walkwithme.data.database.FitnessHistory;
import com.fitaleks.walkwithme.data.database.WalkWithMeProvider;
import com.fitaleks.walkwithme.data.firebase.FirebaseHelper;
import com.fitaleks.walkwithme.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexander on 24.06.16.
 */
public class SyncDataService extends IntentService {

    public SyncDataService() {
        super("SyncDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FirebaseHelper helper = new FirebaseHelper.Builder()
                .addChild(FirebaseHelper.KEY_STEPS)
                .addChild(SharedPrefUtils.getUserUid(this))
                .build();
        helper.getFirebase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getContentResolver().delete(WalkWithMeProvider.History.CONTENT_URI, null, null);
                List<ContentValues> history = new ArrayList<>();

                for (DataSnapshot stepRecord : dataSnapshot.getChildren()) {
                    ContentValues oneHistoryRecord = new ContentValues();
                    oneHistoryRecord.put(FitnessHistory.DATE, stepRecord.getKey());
                    oneHistoryRecord.put(FitnessHistory.NUM_OF_STEPS, (String) stepRecord.getValue());
                    history.add(oneHistoryRecord);
                }
                if (history.size() > 0) {
                    ContentValues[] cVVector = new ContentValues[history.size()];
                    history.toArray(cVVector);
                    getContentResolver()
                            .bulkInsert(WalkWithMeProvider.History.CONTENT_URI, cVVector);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
