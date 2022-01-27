/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.world.block;

import org.bukkit.material.MaterialData;

public class BlockPositionData {
    private final BlockPosition blockPosition;
    private final MaterialData materialData;

    public BlockPositionData(BlockPosition blockPosition, MaterialData materialData) {
        this.blockPosition = blockPosition;
        this.materialData = materialData;
    }

    public BlockPosition getBlockPosition() {
        return this.blockPosition;
    }

    public MaterialData getMaterialData() {
        return this.materialData;
    }
}