package net.drapuria.framework.language.holder;

import net.drapuria.framework.language.LanguageService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageHolderRepository<ID extends Serializable> {

    private final LanguageService service;
    private final Map<ID, LanguageHolder<ID>> holders = new HashMap<>();

    public LanguageHolderRepository(LanguageService service) {
        this.service = service;
    }

    public LanguageHolder<ID> findHolder(final ID id) {
        return this.holders.get(id);
    }

    public Locale findLocale(final ID id) {
        final LanguageHolder<ID> holder = this.findHolder(id);
        if (holder == null)
            return this.service.getDefaultLocale();
        return holder.getLocale();
    }

    public void removeHolder(final ID id) {
        this.holders.remove(id);
    }
}
