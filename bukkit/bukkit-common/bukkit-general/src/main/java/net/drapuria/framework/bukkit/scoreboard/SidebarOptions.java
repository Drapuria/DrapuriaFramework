/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class SidebarOptions {

    public static SidebarOptions defaultOptions() {
        return new SidebarOptions().hook(true)
                .scoreDirectDown(false)
                .showOnJoin(true)
                .showPlayerHealthUnderName(false);
    }

    private boolean hook;
    private boolean scoreDirectDown;
    private long updateDelay;
    private boolean showOnJoin;
    private boolean showPlayerHealthUnderName;
    private String playerHealthDisplayName;

}
