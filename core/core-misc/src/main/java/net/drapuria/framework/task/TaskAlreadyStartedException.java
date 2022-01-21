/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.task;

public class TaskAlreadyStartedException extends Exception {

    public TaskAlreadyStartedException(String message) {
        super(message);
    }
}
