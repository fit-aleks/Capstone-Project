package com.fitaleks.walkwithme.data.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public interface Friends {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";
    @DataType(INTEGER) @NotNull String FRIEND_NAME = "friend_name";
    @DataType(INTEGER) String NUM_OF_STEPS = "num_of_steps";
}
