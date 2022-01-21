/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.impl.module.repository;

import net.drapuria.framework.bukkit.impl.module.parent.BukkitPluginBasedParent;
import net.drapuria.framework.module.Module;
import net.drapuria.framework.module.ModuleAdapter;
import net.drapuria.framework.module.repository.ModuleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BukkitPluginModuleRepository extends ModuleRepository<BukkitPluginBasedParent> {

    private final List<ModuleAdapter> modules = new ArrayList<>();

    private final BukkitPluginBasedParent parent;


    public BukkitPluginModuleRepository(BukkitPluginBasedParent parent) {
        this.parent = parent;
    }

    @Override
    public BukkitPluginBasedParent getParent() {
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

    @Override
    public void init() {

    }

    @Override
    public Class<?> type() {
        return ModuleAdapter.class;
    }

    @Override
    public <Q> Optional<ModuleAdapter> findByQuery(String query, Q value) {
        return Optional.empty();
    }

}
