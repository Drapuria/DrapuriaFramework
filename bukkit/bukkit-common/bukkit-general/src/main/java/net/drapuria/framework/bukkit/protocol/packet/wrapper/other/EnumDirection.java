package net.drapuria.framework.bukkit.protocol.packet.wrapper.other;

public enum EnumDirection {
    DOWN, UP, NORTH, SOUTH, WEST, EAST, NORTH_EAST,
    NORTH_WEST, SOUTH_EAST,
    SOUTH_WEST, WEST_NORTH_WEST,
    NORTH_NORTH_WEST, NORTH_NORTH_EAST,
    EAST_NORTH_EAST, EAST_SOUTH_EAST,
    SOUTH_SOUTH_EAST, SOUTH_SOUTH_WEST,
    WEST_SOUTH_WEST, SELF, NULL;

    public static EnumDirection get(final int i) {
        return values()[i];
    }
}
