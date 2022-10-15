package net.drapuria.framework.bukkit.fake.hologram;

public class TimedPlayerDefinedHologram extends TimedHologram {

    private final PlayerDefinedHologram playerDefinedHologram;

    public TimedPlayerDefinedHologram(final long duration, final PlayerDefinedHologram playerDefinedHologram) {
        super(duration);
        this.playerDefinedHologram = playerDefinedHologram;
        init();
    }

    public TimedPlayerDefinedHologram(final long startTime, final long duration, final PlayerDefinedHologram playerDefinedHologram) {
        super(startTime, duration);
        this.playerDefinedHologram = playerDefinedHologram;
        init();
    }

    @Override
    public void init() {
        HOLOGRAM_SERVICE.addHologram(playerDefinedHologram);
    }

    @Override
    public void delete() {
        HOLOGRAM_SERVICE.removeHologram(playerDefinedHologram);
    }
}
