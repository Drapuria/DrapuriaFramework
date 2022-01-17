package net.drapuria.framework.scheduler.helper;

import lombok.experimental.UtilityClass;
import net.drapuria.framework.scheduler.Timestamp;

@UtilityClass
public class SchedulerHelper {

    public long getTicksFromDuration(long duration, boolean millis) {
        if (millis) {
            duration = duration / 1000;
        }
        long ticks = 20 * duration;
        if (ticks < 0)
            ticks = 0;
        return ticks;
    }

    public long getDurationFromTicks(long ticks, boolean millis) {
        long milliseconds = ticks * 50;
        if (!millis)
            return milliseconds / 1000;
        return milliseconds;
    }

    public long convertTimestampToIteration(Timestamp timestamp, long iterations) {
        switch (timestamp) {
            case END:
                return 0;
            case MID:
                return iterations / 2;
            case BEGINNING:
                return iterations - 1;
        }
        return -1;
    }

}
