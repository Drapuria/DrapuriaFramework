package net.drapuria.framework.language.impl;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.language.LanguageHolder;

import java.io.File;

@Component
public class DrapuriaLanguageHolder implements LanguageHolder<DrapuriaPlatform> {
    @Override
    public DrapuriaPlatform holder() {
        return DrapuriaCommon.PLATFORM;
    }

    @Override
    public File languageFolder() {
        return new File(this.holder().getDataFolder(), "lang");
    }
}