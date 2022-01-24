package net.drapuria.framework.bukkit.reflection.version;

import java.util.Arrays;

public enum PlayerVersion {
    v1_7(4, 5),
    v1_8(47),
    v1_9(107, 108, 109, 110),
    v1_10(210),
    v1_11(315, 316),
    v1_12(335, 338, 340),
    v1_13(393, 401, 404),
    V_1_14(477, 480, 485, 490, 498),
    V_1_15(573, 575, 578),
    V_1_16(735, 751, 753, 754),
    V_1_17(755, 756),
    V_1_18(757),
    ;

    private Integer[] rawVersion;

    PlayerVersion(Integer... rawVersionNumbers) {
        this.rawVersion = rawVersionNumbers;
    }

    public static PlayerVersion getVersionFromRaw(Integer input) {
        PlayerVersion[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            PlayerVersion playerVersion = var1[var3];
            if (Arrays.asList(playerVersion.rawVersion).contains(input)) {
                return playerVersion;
            }
        }

        return v1_8;
    }

    public Integer[] getRawVersion() {
        return this.rawVersion;
    }
}

