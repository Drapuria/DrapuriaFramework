package net.drapuria.framework.bukkit.fake.entity.npc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NameTagType {

    HIDDEN(true),
    NAME_TAG(false),
    HOLOGRAM(true),
    ;

    private final boolean hideHologram;

}
