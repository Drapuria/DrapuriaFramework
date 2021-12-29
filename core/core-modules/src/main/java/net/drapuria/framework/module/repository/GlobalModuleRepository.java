package net.drapuria.framework.module.repository;

import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.parent.PlatformBasedParent;
import net.drapuria.framework.module.service.ModuleService;
import net.drapuria.framework.repository.InMemoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GlobalModuleRepository extends ModuleRepository<PlatformBasedParent> {

    private final ModuleService service;

    public GlobalModuleRepository(ModuleService service) {
        this.service = service;
    }

    public List<Module> getModules() {
        final List<Module> list = new ArrayList<>();
        service.getRepositories().values().stream()
                .map(InMemoryRepository::findAll)
                .forEach(moduleAdapters -> moduleAdapters.forEach(adapter -> list.add(adapter.getModule())));
        return list;
    }

    public ModuleAdapter findAdapterByName(String name) {
        return null;
    }

    public ModuleAdapter getAdapterFromModule(Module module) {
        return null;
    }

    public Module findByName(String name) {
        return getModules()
                .stream()
                .filter(module -> module.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Optional<ModuleAdapter> findById(String s) {
        return service.getRepositories()
                .values()
                .stream()
                .map(repository -> repository.findById(s)
                        .orElse(null))
                .filter(Objects::nonNull).findFirst();
    }
    public void addModule(ModuleAdapter module) {
        throw new UnsupportedOperationException("Cannot add module into this repository");
    }

    public void removeModule(Module module) {
        this.service.getRepositories()
                .values()
                .forEach(repository -> repository.deleteById(module.getName()));
    }

    public void removeModule(ModuleAdapter adapter) {
        this.service.getRepositories()
                .values()
                .forEach(repository -> repository.deleteById(adapter.getModuleName()));
    }

    @Override
    public void init() {

    }

    @Override
    public <Q> Optional<ModuleAdapter> findByQuery(String query, Q value) {
        return Optional.empty();
    }

    @Override
    public long count() {
        return getModules().size();
    }

    @Override
    public PlatformBasedParent getParent() {
        return null;
    }
}
