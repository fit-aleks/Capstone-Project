package com.fitaleks.walkwithme.data.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public interface FriendsHistory {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";
    @DataType(TEXT) String GOOGLE_ID = "google_id";
    @DataType(INTEGER) String TIME = "time";
    @DataType(INTEGER) String STEPS = "steps";
}
