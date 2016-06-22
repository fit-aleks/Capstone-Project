package com.fitaleks.walkwithme.data.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public interface Friends {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";
    @DataType(TEXT) @NotNull @Unique(onConflict = ConflictResolutionType.REPLACE) String FRIEND_NAME = "friend_name";
    @DataType(INTEGER) String NUM_OF_STEPS = "num_of_steps";
    @DataType(TEXT) String GOOGLE_USER_ID = "user_id";
    @DataType(TEXT) String PHOTO = "photo";
}
