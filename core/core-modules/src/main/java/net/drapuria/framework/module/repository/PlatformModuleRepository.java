package net.drapuria.framework.module.repository;

import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.parent.PlatformBasedParent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlatformModuleRepository extends ModuleRepository<PlatformBasedParent> {
    private final PlatformBasedParent parent;

    private final List<ModuleAdapter> modules = new ArrayList<>();

    public PlatformModuleRepository(PlatformBasedParent parent) {
        this.parent = parent;
    }

    @Override
    public PlatformBasedParent getParent() {
        return parent;
    }

    public List<Module> getModules() {
        return modules.stream()
                .map(ModuleAdapter::getModule)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ModuleAdapter findAdapterByName(String name) {
        return modules.stream()
                .filter(module -> module.getModuleData().name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public ModuleAdapter getAdapterFromModule(Module module) {
        return modules.stream()
                .filter(adapter -> adapter.getModule() == module)
                .findFirst()
                .orElse(null);
    }

    public Module findByName(String name) {
        return modules.stream()
                .filter(module -> module.getModuleData().name().equalsIgnoreCase(name))
                .map(ModuleAdapter::getModule)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public void addModule(ModuleAdapter module) {
        this.modules.add(module);
    }

    public void removeModule(Module module) {
        this.modules.removeIf(adapter -> adapter.getModule() == module);
    }

    public void removeModule(ModuleAdapter adapter) {
        this.modules.remove(adapter);
    }

    @Override
    public void init() {

    }
}