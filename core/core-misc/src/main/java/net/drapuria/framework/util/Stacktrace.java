/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

@UtilityClass
public class Stacktrace {

    private static final Logger LOGGER = LogManager.getLogger("DrapuriaFramework-Error");

    public void print(Throwable throwable) {
        LOGGER.error("An error occurs! : " + getStacktrace(throwable));
    }

    public String getStacktrace(Throwable throwable) {
        StringWriter stack = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stack));

        return stack.toString();
    }

}
