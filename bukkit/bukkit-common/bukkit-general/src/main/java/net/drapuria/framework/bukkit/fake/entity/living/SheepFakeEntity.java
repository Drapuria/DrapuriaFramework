package net.drapuria.framework.bukkit.fake.entity.living;

import lombok.Getter;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityOptions;
import net.drapuria.framework.bukkit.fake.entity.FakeEntityPool;
import net.minecraft.server.v1_8_R3.EnumColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Getter
public class SheepFakeEntity extends LivingFakeEntity {

    private EnumColor color;

    public SheepFakeEntity(int entityId, FakeEntityOptions options, Location location, FakeEntityPool entityPool, EntityType entityType) {
        super(entityId, options, location, entityPool, entityType);
        this.color = EnumColor.WHITE;
        byte color = (byte) this.color.getColorIndex();
        super.dataWatcher.setObject(16, color);

    }

    public void setColor(EnumColor color) {
        this.color = color;
        byte byteColor = (byte) color.getColorIndex();
        super.dataWatcher.setObject(16, byteColor);
    }
}
