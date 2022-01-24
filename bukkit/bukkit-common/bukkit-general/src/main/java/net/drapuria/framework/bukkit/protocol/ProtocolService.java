/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.protocol;

import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.protocol.packet.helper.VersionHelper;

@Service(name = "protocolService")
@Getter
public class ProtocolService {

    private ProtocolImplementation<?> protocolImplementation;

    @Setter
    private VersionHelper versionHelper;

    public static ProtocolService protocolService;

    @PreInitialize
    public void preInit() {
        protocolService = this;
    }
}
