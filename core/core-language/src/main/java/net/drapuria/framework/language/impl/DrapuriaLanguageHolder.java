package net.drapuria.framework.language.impl;

import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.DrapuriaPlatform;
import net.drapuria.framework.beans.annotation.Component;
import net.drapuria.framework.language.LanguageHolder;

@Component
public class DrapuriaLanguageHolder implements LanguageHolder<DrapuriaPlatform> {
    @Override
    public DrapuriaPlatform holder() {
        return DrapuriaCommon.PLATFORM;
    }
}
