/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the times in ticks
 */
@Getter
@RequiredArgsConstructor
public enum TickTime {

    TICK(1),
    SECONDS(20),
    MINUTE(20 * 60),
    HOUR(72000),
    DAY(1728000),
    WEEK(12096000)
    ;

    private final long ticks;
}
