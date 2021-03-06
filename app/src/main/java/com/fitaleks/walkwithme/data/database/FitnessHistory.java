package com.fitaleks.walkwithme.data.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;

/**
 * Created by alexanderkulikovskiy on 23.04.16.
 */
public interface FitnessHistory {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";
//    @DataType(TEXT) @Unique(onConflict = ConflictResolutionType.REPLACE) @NotNull String NAME = "name";
    @DataType(INTEGER) @NotNull String DATE = "date_in_ms";
    @DataType(INTEGER) @NotNull String NUM_OF_STEPS = "num_of_steps";
}
