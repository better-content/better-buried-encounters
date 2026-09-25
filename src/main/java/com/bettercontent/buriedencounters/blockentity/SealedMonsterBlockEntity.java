package com.bettercontent.buriedencounters.blockentity;

import com.bettercontent.buriedencounters.registry.BuriedRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class SealedMonsterBlockEntity extends BlockEntity {
    private static final ResourceLocation FALLBACK = new ResourceLocation("minecraft", "silverfish");
    private ResourceLocation entityId = FALLBACK;

    public SealedMonsterBlockEntity(BlockPos pos, BlockState state) {
        super(BuriedRegistries.SEALED_MONSTER_ENTITY.get(), pos, state);
    }

    public ResourceLocation entityId() {
        return entityId;
    }

    public void setEntityId(ResourceLocation entityId) {
        this.entityId = entityId == null ? FALLBACK : entityId;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Entity", entityId.toString());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ResourceLocation parsed = ResourceLocation.tryParse(tag.getString("Entity"));
        entityId = parsed == null ? FALLBACK : parsed;
    }
}
