package net.drapuria.framework.module.service;

import net.drapuria.framework.module.repository.ModuleRepository;
import net.drapuria.framework.plugin.AbstractPlugin;
import net.drapuria.framework.services.PostInitialize;
import net.drapuria.framework.services.PreInitialize;
import net.drapuria.framework.services.Service;

import java.util.HashMap;
import java.util.Map;

@Service(name = "Modules")
public class ModuleService {

    public static ModuleService getInstance;

    private final Map<AbstractPlugin, ModuleRepository> repositories = new HashMap<>();
    @PreInitialize
    public void preInit() {
        getInstance = this;
    }

    @PostInitialize
    public void init() {

    }

}
