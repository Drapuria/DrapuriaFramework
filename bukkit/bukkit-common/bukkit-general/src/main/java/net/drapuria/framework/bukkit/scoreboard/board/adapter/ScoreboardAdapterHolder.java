/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.scoreboard.board.adapter;

import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.bukkit.scoreboard.ScoreboardService;

public class ScoreboardAdapterHolder extends ComponentHolder {

    @Override
    public void onEnable(Object instance) {
        if (instance instanceof ScoreboardAdapter) {
            ScoreboardAdapter adapter = (ScoreboardAdapter) instance;
            if (adapter instanceof DefaultAdapter) {
                ScoreboardService.getService.setDefaultAdapter(adapter);
            }
            ScoreboardService.getService.addAdapter(adapter);
        }
    }

    @Override
    public void onDisable(Object instance) {
        if (instance instanceof ScoreboardAdapter) {
            ScoreboardAdapter adapter = (ScoreboardAdapter) instance;
            if (ScoreboardService.getService.getDefaultAdapter() == adapter) {
                ScoreboardService.getService.setDefaultAdapter(null);
            }
            ScoreboardService.getService.clearAdapter(adapter);
            ScoreboardService.getService.removeAdapter(adapter);
        }
    }

    @Override
    public Class<?>[] type() {
        return new Class[] {DefaultAdapter.class};
    }
}
