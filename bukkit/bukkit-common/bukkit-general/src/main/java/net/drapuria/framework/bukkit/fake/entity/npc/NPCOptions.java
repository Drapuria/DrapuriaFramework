package net.drapuria.framework.bukkit.fake.entity.npc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NPCOptions {

    private NPCProfile npcProfile;
    private SkinType skinType;
    private NameTagType nameTagType;

}
