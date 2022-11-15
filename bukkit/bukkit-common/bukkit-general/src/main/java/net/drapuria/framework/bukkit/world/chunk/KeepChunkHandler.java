/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.world.chunk;

import net.drapuria.framework.beans.annotation.PostDestroy;
import net.drapuria.framework.beans.annotation.PostInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.Drapuria;
import net.drapuria.framework.bukkit.listener.events.EventSubscription;
import net.drapuria.framework.bukkit.listener.events.Events;
import net.drapuria.framework.util.LongHash;
import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashSet;
import java.util.Set;

@Service(name = "chunkHandler")
public class KeepChunkHandler {

    private Set<Long> chunksToKeep;
    private EventSubscription<ChunkUnloadEvent> eventSubscription;

    @PostInitialize
    public void init() {
        this.chunksToKeep = new HashSet<>();

        this.eventSubscription = Events.subscribe(ChunkUnloadEvent.class)
                .listen((sub, event) -> {
                    Chunk chunk = event.getChunk();
                    if (isChunkToKeep(chunk.getX(), chunk.getZ())) {
                        event.setCancelled(true);
                    }
                }).build(Drapuria.PLUGIN);
    }

    @PostDestroy
    public void shutdown() {
        this.eventSubscription.unregister();
    }

    public void addChunk(int x, int z) {
        this.chunksToKeep.add(LongHash.toLong(x, z));
    }

    public void removeChunk(int x, int z) {
        this.chunksToKeep.remove(LongHash.toLong(x, z));
    }

    public boolean isChunkToKeep(int x, int z) {
        return this.chunksToKeep.contains(LongHash.toLong(x, z));
    }


}
