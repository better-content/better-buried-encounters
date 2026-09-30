package com.bettercontent.betterburiedencounters.block;

import com.bettercontent.betterburiedencounters.blockentity.SealedMonsterBlockEntity;
import com.bettercontent.betterburiedencounters.registry.BuriedRegistries;
import com.bettercontent.betterburiedencounters.worldgen.ExplosionRemovalTracker;
import com.bettercontent.betterburiedencounters.worldgen.MonsterSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;

public final class SealedMonsterBlock extends Block implements EntityBlock {
    public static final IntegerProperty PALETTE = IntegerProperty.create("palette", 0, 2);

    public SealedMonsterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PALETTE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PALETTE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BuriedRegistries.SEALED_MONSTER_ENTITY.get().create(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && !newState.is(this)
                && level.getBlockEntity(pos) instanceof SealedMonsterBlockEntity sealed) {
            ExplosionRemovalTracker.consume(level, pos);
            MonsterSpawner.spawn(level, pos, sealed.entityId());
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
