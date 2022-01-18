package net.drapuria.framework.bukkit.player;

import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.repository.InMemoryRepository;

import java.util.UUID;

@Service(name = "drapuriaPlayerRepository")
public class PlayerRepository extends InMemoryRepository<DrapuriaPlayer<?>, UUID> {

    public static PlayerRepository getRepository;

    @Override
    public void init() {
        getRepository = this;
    }

    @Override
    public Class<?> type() {
        return DrapuriaPlayer.class;
    }
}
