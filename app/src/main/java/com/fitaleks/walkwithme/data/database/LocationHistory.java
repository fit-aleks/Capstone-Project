package com.fitaleks.walkwithme.data.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;

/**
 * Created by alexanderkulikovskiy on 05.05.16.
 */
public interface LocationHistory {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";
    @DataType(REAL) @NotNull String LATITUDE = "latitude";
    @DataType(REAL) @NotNull String LONGITUDE = "longitude";
}
