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
    HOUR(20 * 60 * 60),
    DAY(20 * 60 * 60 * 24),
    WEEK(20 * 60 * 60 * 24 * 7)
    ;

    private final int ticks;
}
