package net.drapuria.framework.language.holder.impl;

import net.drapuria.framework.language.holder.LanguageHolder;

import java.util.Locale;
import java.util.UUID;

public class DrapuriaLanguageHolder implements LanguageHolder<UUID> {
    private final UUID uuid;
    private Locale locale = Locale.ENGLISH;

    public DrapuriaLanguageHolder(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public Locale setLocale(Locale locale) {
        return this.locale = locale;
    }

    @Override
    public UUID recognizer() {
        return this.uuid;
    }
}