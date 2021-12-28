package net.drapuria.framework.module.repository;

import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.repository.CachedRepository;

public abstract class ModuleRepository<P extends ModuleParent<?>> extends CachedRepository<ModuleAdapter, String> {

    public abstract P getParent();

}
