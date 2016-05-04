package com.fitaleks.walkwithme.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by alexanderkulikovskiy on 23.04.16.
 */
@Database(version = WalkWithMeDatabase.VERSION,
    packageName = "com.fitaleks.walkwithme.provider")
public final class WalkWithMeDatabase {
    private WalkWithMeDatabase(){}

    public static final int VERSION = 1;

    @Table(FitnessHistory.class)
    public static final String HISTORY = "history";

}
