package com.fitaleks.walkwithme.database;

import android.content.ContentValues;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.TableEndpoint;
import net.simonvt.schematic.annotation.Where;

/**
 * Created by alexanderkulikovskiy on 23.04.16.
 */
@ContentProvider(authority = WalkWithMeProvider.AUTHORITY,
        database = WalkWithMeDatabase.class,
        packageName = "com.fitaleks.walkwithme.provider")
public final class WalkWithMeProvider {
    public static final String AUTHORITY = "com.fitaleks.walkwithme.database.WalkWithMeProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String HISTORY = "history";
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

//        @InexactContentUri(
//                name = "HISTORY_BY_DAY",
//                path = Path.HISTORY + "/#/#",
//                type = "vnd.android.cursor.dir/list",
//                whereColumn = {},
//                pathSegment = {},
//                where = FitnessHistory.DATE + " > " + )
//        public static Uri betweenDates(long firstDate, long lastDate) {
//            return buildUri(Path.HISTORY, Long.toString(firstDate), Long.toString(lastDate));
//        }

        @NotifyInsert(paths = Path.HISTORY)
        public static Uri[] onInsert(ContentValues values) {
            return new Uri[]{};
        }


    }
}
