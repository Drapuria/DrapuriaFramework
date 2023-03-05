package net.drapuria.framework.language.holder.listener;

import net.drapuria.framework.language.holder.LanguageHolderRepository;

public interface HolderRepositoryChangeListener {

    void onChange(LanguageHolderRepository<?> from, LanguageHolderRepository<?> to);

}
