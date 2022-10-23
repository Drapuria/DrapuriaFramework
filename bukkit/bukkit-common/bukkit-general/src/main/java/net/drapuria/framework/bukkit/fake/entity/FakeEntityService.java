package net.drapuria.framework.bukkit.fake.entity;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.drapuria.framework.DrapuriaCommon;
import net.drapuria.framework.beans.annotation.PostDestroy;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.fake.entity.exceptions.FakeEntityPoolAlreadyRegisteredException;
import net.drapuria.framework.bukkit.fake.entity.exceptions.FakeEntityPoolNotFoundException;
import net.drapuria.framework.scheduler.Scheduler;
import net.drapuria.framework.scheduler.SchedulerService;
import net.drapuria.framework.scheduler.TickTime;
import net.drapuria.framework.scheduler.factory.SchedulerFactory;
import net.drapuria.framework.scheduler.provider.AbstractSchedulerProvider;
import net.drapuria.framework.util.Stacktrace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Service(name = "fakeEntityService")
public class FakeEntityService {

    private FakeEntityPool defaultPool;
    private final Map<String, FakeEntityPool> pools = new HashMap<>();
    @SuppressWarnings("SpellCheckingInspection")
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    @Getter
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("FakeEntity-Render-Pool-%d")
            .build());

    @PreInitialize
    public void init() {
    }

    @SneakyThrows
    @PostInitialize
    public void registerDefaultPool() {
        this.registerPool(this.defaultPool = new FakeEntityPool(Drapuria.PLUGIN, DrapuriaCommon.METADATA_PREFIX + "POOL"));
    }

    @PostDestroy
    public void stop() {
        executorService.shutdown();
    }

    public FakeEntityPool getPool(final String name) {
        return this.pools.get(name);
    }

    public Collection<FakeEntityPool> getPools() {
        return this.pools.values();
    }

    public void registerPool(final FakeEntityPool pool) throws FakeEntityPoolAlreadyRegisteredException {
        if (this.pools.containsKey(pool.getName()))
            throw new FakeEntityPoolAlreadyRegisteredException("FakeEntityPool with name '" + pool.getName() + "' already registered!");
        this.pools.put(pool.getName(), pool);
    }

    public FakeEntityPool getDefaultPool() {
        return this.defaultPool;
    }

    public void setDefaultPool(FakeEntityPool defaultPool) {
        this.defaultPool = defaultPool;
    }

    public void removePool(final FakeEntityPool pool) {
        this.pools.remove(pool.getName());
    }

    public boolean hasCooldown(final UUID player) {
        return this.cooldowns.containsKey(player) && this.cooldowns.get(player) > System.currentTimeMillis();
    }

    public void removeCooldown(final UUID player) {
        this.cooldowns.remove(player);
    }

    public void setCooldowns(final UUID player, long cooldown) {
        this.cooldowns.put(player, System.currentTimeMillis() + cooldown);
    }

    public void removePool(final String name) throws FakeEntityPoolNotFoundException {
        if (!this.pools.containsKey(name))
            throw new FakeEntityPoolNotFoundException("FakeEntityPool with name '" + name + "' does not exist!");
        this.removePool(this.getPool(name));
    }

    public int getFreeEntityId() {
        final AtomicInteger id = new AtomicInteger();
        do {
            id.set(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        } while (this.pools.values().stream().anyMatch(pool -> pool.getEntities().containsKey(id.get())));
        return id.get();
    }

}
