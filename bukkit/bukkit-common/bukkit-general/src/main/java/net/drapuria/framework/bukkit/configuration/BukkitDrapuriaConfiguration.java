package net.drapuria.framework.bukkit.configuration;

import lombok.Data;
import lombok.Getter;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.configuration.development.DevelopmentConfiguration;
import net.drapuria.framework.bukkit.impl.configuration.BukkitYamlConfiguration;
import net.drapuria.framework.configuration.yaml.annotation.Format;
import net.drapuria.framework.configuration.yaml.format.FieldNameFormatters;

import java.io.File;

@Format(value = FieldNameFormatters.LOWER_UNDERSCORE)
@Getter
public class BukkitDrapuriaConfiguration extends BukkitYamlConfiguration {


    private DevelopmentConfiguration developmentConfiguration = new DevelopmentConfiguration();

    public BukkitDrapuriaConfiguration() {
        super(new File(DrapuriaCommon.PLATFORM.getDataFolder(), "drapuria-bukkit.yml").toPath());
        loadAndSave();
    }
}
