/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm.impl;

import lombok.AllArgsConstructor;
import net.drapuria.framework.database.orm.SqlColumnTransformer;

@AllArgsConstructor
public class LegacyColumnTransformer implements SqlColumnTransformer {

    private String readString;
    private String writeString;

    @Override
    public String getReadString() {
        return readString;
    }

    @Override
    public String getWriteString() {
        return writeString;
    }
}
