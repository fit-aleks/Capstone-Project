package com.fitaleks.walkwithme.data.database;

import android.content.ContentValues;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by alexanderkulikovskiy on 23.04.16.
 */
@ContentProvider(authority = WalkWithMeProvider.AUTHORITY,
        database = WalkWithMeDatabase.class,
        packageName = "com.fitaleks.walkwithme.data.provider")
public final class WalkWithMeProvider {
    public static final String AUTHORITY = "com.fitaleks.walkwithme.data.database.WalkWithMeProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String HISTORY = "history";
        String FRIENDS = "friends";
        String FRIENDS_HISTORY = "friends_history";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = WalkWithMeDatabase.HISTORY)
    public static class History {
        @ContentUri(
                path = Path.HISTORY,
                type = "vnd.android.cursor.dir/list",
                defaultSort = FitnessHistory.DATE + " DESC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.HISTORY);

        @InexactContentUri(
                name = "HISTORY_ID",
                path = Path.HISTORY + "/#",
                type = "vnd.android.cursor.dir/list",
                whereColumn = FitnessHistory.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.HISTORY, Long.toString(id));
        }

        @ContentUri(
                path = Path.HISTORY + "/by_days",
                type = "vnd.android.cursor.dir/list",
                groupBy = "strftime('%d-%m-%Y', " + FitnessHistory.DATE + "/1000, 'unixepoch', 'localtime')"
        )
        public static final Uri BY_DAYS_URI = buildUri(Path.HISTORY, "by_days");

        @NotifyInsert(paths = Path.HISTORY)
        public static Uri[] onInsert(ContentValues values) {
            return new Uri[]{};
        }

    }

    @TableEndpoint(table = WalkWithMeDatabase.FRIENDS)
    public static class FriendsTable {
        @ContentUri(
                path = Path.FRIENDS,
                type = "vnd.android.cursor.dir/list",
                defaultSort = Friends.FRIEND_NAME + " DESC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.FRIENDS);

        @NotifyInsert(paths = Path.FRIENDS)
        public static Uri[] onInsert(ContentValues values) {
            return new Uri[]{};
        }

        @InexactContentUri(
                name = "FRIEND_ITEM",
                path = Path.FRIENDS + "/friend_item" + "/*",
                type = "vnd.adnroid.cursor.dir/item",
                whereColumn = Friends.GOOGLE_USER_ID,
                pathSegment = 2)
        public static Uri withFriendGoogleId(final String friendsGoogleId) {
            return buildUri(Path.FRIENDS, "friend_item", friendsGoogleId);
        }

        @ContentUri(
                path = Path.FRIENDS + "/with_steps",
                type = "vnd.android.cursor.dir/list",
                join = "JOIN " + WalkWithMeDatabase.FRIENDS_HISTORY + " ON " + Friends.GOOGLE_USER_ID + " LIKE " + FriendsHistory.GOOGLE_ID,
                groupBy = Friends.GOOGLE_USER_ID
        )
        public static Uri friendsWithSteps = buildUri(Path.FRIENDS, "with_steps");
    }

    @TableEndpoint(table = WalkWithMeDatabase.FRIENDS_HISTORY)
    public static class FriendsHistoryTable {
        @ContentUri(
                path = Path.FRIENDS_HISTORY,
                type = "vnd.android.cursor.dir/list",
                defaultSort = FriendsHistory.GOOGLE_ID + " DESC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.FRIENDS_HISTORY);

        @InexactContentUri(
                name = "FRIEND_HISTORY",
                path = Path.FRIENDS_HISTORY + "/*",
                type = "vnd.android.cursor.dir/list",
                whereColumn = FriendsHistory.GOOGLE_ID,
                pathSegment = 1
        )
        public static Uri withId(final String googleId) {
            return buildUri(Path.FRIENDS_HISTORY, googleId);
        }
    }
}
