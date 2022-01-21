/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.orm;

public interface SqlColumnTransformer {

    String getReadString();

    String getWriteString();


}
