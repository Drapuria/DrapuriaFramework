/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard;

import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreDestroy;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;

@Service(name = "scoreboard")
public class ScoreboardService {

    public static ScoreboardService getService;

    @PreInitialize
    public void preInit() {
        getService = this;
    }

    @PostInitialize
    public void init() {

    }

    @PreDestroy
    public void shutdown() {

    }

}
