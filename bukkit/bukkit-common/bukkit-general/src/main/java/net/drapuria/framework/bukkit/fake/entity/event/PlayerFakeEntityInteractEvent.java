package net.drapuria.framework.bukkit.fake.entity.event;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.drapuria.framework.bukkit.events.BaseEvent;
import net.drapuria.framework.bukkit.fake.entity.FakeEntity;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class PlayerFakeEntityInteractEvent extends BaseEvent {

    private final Player player;
    private final FakeEntity entity;
    private final EnumWrappers.EntityUseAction action;

}
