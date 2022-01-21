/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.plugin;

import java.nio.file.Path;

public interface ClassPathAppender {

    void addJarToClassPath(Path path);


    default void close() {

    }

}
