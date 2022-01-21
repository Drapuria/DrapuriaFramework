/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.scheduler.action;

import lombok.Getter;

import java.util.function.BiConsumer;

@Getter
public final class RepeatedAction<T> {

    private final boolean lastTick;
    private final boolean firstTick;
    private final boolean always;
    private final int division;
    private final long remainder;
    private final BiConsumer<Long, T> action;
    private ActionPriority priority;

    public RepeatedAction(boolean lastTick, boolean firstTick, boolean always, int division, long remainder, BiConsumer<Long, T> action) {
        this(lastTick, firstTick, always, division, remainder, action, ActionPriority.NORMAL);
    }

    public RepeatedAction(boolean lastTick, boolean firstTick, boolean always, int division, long remainder, BiConsumer<Long, T> action, ActionPriority priority) {
        this.lastTick = lastTick;
        this.firstTick = firstTick;
        this.always = always;
        this.division = division;
        this.remainder = remainder;
        this.action = action;
        this.priority = priority;
    }
}
