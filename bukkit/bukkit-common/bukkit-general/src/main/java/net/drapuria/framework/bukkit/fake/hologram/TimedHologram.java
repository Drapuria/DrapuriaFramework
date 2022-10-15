package net.drapuria.framework.bukkit.fake.hologram;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.drapuria.framework.DrapuriaCommon;

@Getter
@Setter
@NoArgsConstructor
public abstract class TimedHologram {

    static final HologramService HOLOGRAM_SERVICE = DrapuriaCommon.getBean(HologramService.class);


    private long startTime;
    private long duration;

    public TimedHologram(final long duration) {
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public TimedHologram(final long startTime, final long duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public boolean hasExpired() {
        return this.getStartTime() + this.getDuration() < System.currentTimeMillis();
    }

    public void check() {
        if (hasExpired())
            delete();
    }

    public abstract void init();

    public abstract void delete();

}
