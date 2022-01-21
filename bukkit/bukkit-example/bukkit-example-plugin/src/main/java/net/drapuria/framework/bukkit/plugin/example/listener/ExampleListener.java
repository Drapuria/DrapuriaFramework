/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.plugin.example.listener;

import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

@Component
public class ExampleListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

}
