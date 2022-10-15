package net.drapuria.framework.bukkit.fake.hologram;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TimedGlobalHologram extends TimedHologram {

    private GlobalHologram globalHologram;

    public TimedGlobalHologram(final long duration, final GlobalHologram globalHologram) {
        super(duration);
        this.globalHologram = globalHologram;
        init();
    }

    public TimedGlobalHologram(final long startTime, final long duration, final GlobalHologram globalHologram) {
        super(startTime, duration);
        this.globalHologram = globalHologram;
        init();
    }

    @Override
    public void init() {
        HOLOGRAM_SERVICE.addHologram(globalHologram);
    }

    @Override
    public void delete() {
        HOLOGRAM_SERVICE.removeHologram(globalHologram);
    }
}
