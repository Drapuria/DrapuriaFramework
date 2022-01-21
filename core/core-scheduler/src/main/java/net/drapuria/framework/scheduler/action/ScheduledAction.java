/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.scheduler.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public final class ScheduledAction<T> {

    private final Consumer<T> action;
    private final ActionPriority priority;

    public ScheduledAction(Consumer<T> action) {
        this(action, ActionPriority.NORMAL);
    }

    public void accept(T object) {
        action.accept(object);
    }

}
