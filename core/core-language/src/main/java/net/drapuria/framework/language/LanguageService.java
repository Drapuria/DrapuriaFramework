package net.drapuria.framework.language;

import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.beans.component.ComponentHolder;
import net.drapuria.framework.beans.component.ComponentRegistry;

import java.util.HashSet;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

@Service(name = "languageService")
public class LanguageService {


    private final Set<LanguageHolder<?>> languageHolders = new HashSet<>();

    @PreInitialize
    public void init() {
       this.registerComponentHolder();
    }

    @PostInitialize
    public void loadInternals() {

    }

    private void registerComponentHolder() {
        ComponentRegistry.registerComponentHolder(new ComponentHolder() {
            @Override
            public void onEnable(Object instance) {
                languageHolders.add((LanguageHolder<?>) instance);
            }

            @Override
            public Class<?>[] type() {
                return new Class[]{LanguageHolder.class};
            }
        });
    }

}
