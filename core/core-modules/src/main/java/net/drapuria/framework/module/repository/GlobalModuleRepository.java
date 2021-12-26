package net.drapuria.framework.module.repository;

import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.parent.ModuleParent;
import net.drapuria.framework.module.service.ModuleService;

import java.util.ArrayList;
import java.util.List;

public class GlobalModuleRepository implements ModuleRepository {

    private final ModuleService service;

    public GlobalModuleRepository(ModuleService service) {
        this.service = service;
    }

    @Override
    public ModuleParent<?> getParent() {
        return null;
    }

    @Override
    public List<Module> getModules() {
        final List<Module> list = new ArrayList<>();
        service.getRepositories()
                .values()
                .stream()
                .map(ModuleRepository::getModules)
                .forEach(list::addAll);
        return list;
    }

    @Override
    public ModuleAdapter findAdapterByName(String name) {
        return null;
    }

    @Override
    public ModuleAdapter getAdapterFromModule(Module module) {
        return null;
    }

    @Override
    public Module findByName(String name) {
        return service.getRepositories()
                .values()
                .stream()
                .map(ModuleRepository::getModules)
                .filter(modules -> modules.stream().anyMatch(module -> module.getName().equalsIgnoreCase(name)))
                .map(modules -> modules.stream().findFirst().orElse(null)).findFirst().orElse(null);
    }



    @Override
    public void addModule(ModuleAdapter module) {
        throw new UnsupportedOperationException("Cannot add module into this repository");
    }

    @Override
    public void removeModule(Module module) {
        this.service.getRepositories()
                .values()
                .forEach(repository -> repository.removeModule(module));
    }

    @Override
    public void removeModule(ModuleAdapter adapter) {
        this.service.getRepositories()
                .values()
                .forEach(repository -> repository.removeModule(adapter));
    }
}
