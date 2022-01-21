/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.module.repository;

import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.repository.InMemoryRepository;

public abstract class ModuleRepository<P extends ModuleParent<?>> extends InMemoryRepository<ModuleAdapter, String> {

    public abstract P getParent();

}
