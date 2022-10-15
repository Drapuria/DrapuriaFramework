package net.drapuria.framework.bukkit.fake.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.drapuria.framework.bukkit.fake.FakeShowType;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class FakeEntityOptions {

    private String name;
    private String displayName;
    private boolean playerLook;
    private FakeShowType showType;
    private boolean editable;
    private boolean handleEventAsync = false;
    @Setter(AccessLevel.NONE)
    private final Set<FakeEntitySpawnHandler> spawnHandlers = new HashSet<>();


    public void addSpawnHandler(final FakeEntitySpawnHandler handler) {
        this.spawnHandlers.add(handler);
    }

    public void removeSpawnHandler(final FakeEntitySpawnHandler handler) {
        this.spawnHandlers.remove(handler);
    }

}
