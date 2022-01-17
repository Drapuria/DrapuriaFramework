package net.drapuria.framework.scheduler.action;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@Data
@RequiredArgsConstructor
public final class RepeatedAction<T> {

    private final boolean lastTick;
    private final boolean firstTick;
    private final boolean always;
    private final int division;
    private final long remainder;
    private final BiConsumer<Long, T> action;

}
