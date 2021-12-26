package net.drapuria.framework.module.repository;

import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.parent.ModuleParent;

import java.util.List;

public interface ModuleRepository<P extends ModuleParent<?>> {

    P getParent();

    List<Module> getModules();

    ModuleAdapter findAdapterByName(String name);

    ModuleAdapter getAdapterFromModule(Module module);

    Module findByName(String name);

    void addModule(ModuleAdapter module);

    void removeModule(Module module);

    void removeModule(ModuleAdapter adapter);


}
