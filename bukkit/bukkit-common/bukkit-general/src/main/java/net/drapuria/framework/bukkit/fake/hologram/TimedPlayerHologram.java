package net.drapuria.framework.bukkit.fake.hologram;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TimedPlayerHologram extends TimedHologram {


    private PlayerHologram playerHologram;

    public TimedPlayerHologram(final long duration, final PlayerHologram playerHologram) {
        super(duration);
        this.playerHologram = playerHologram;
        init();
    }

    public TimedPlayerHologram(final long startTime, final long duration, final PlayerHologram playerHologram) {
        super(startTime, duration);
        this.playerHologram = playerHologram;
        init();
    }

    @Override
    public void init() {
        HOLOGRAM_SERVICE.addHologram(playerHologram);
    }

    @Override
    public void delete() {
        HOLOGRAM_SERVICE.removeHologram(playerHologram);
    }
}
