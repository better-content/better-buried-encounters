package com.bettercontent.buriedencounters.registry;

import com.bettercontent.buriedencounters.BuriedEncounters;
import com.bettercontent.buriedencounters.block.SealedMonsterBlock;
import com.bettercontent.buriedencounters.blockentity.SealedMonsterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class BuriedRegistries {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, BuriedEncounters.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BuriedEncounters.MOD_ID);

    public static final RegistryObject<SealedMonsterBlock> SEALED_MONSTER = BLOCKS.register(
            "sealed_monster", () -> new SealedMonsterBlock(Block.Properties.of()
                    .mapColor(MapColor.DIRT).strength(0.8F).sound(SoundType.GRAVEL)));
    public static final RegistryObject<BlockEntityType<SealedMonsterBlockEntity>> SEALED_MONSTER_ENTITY =
            BLOCK_ENTITIES.register("sealed_monster", () -> BlockEntityType.Builder.of(
                    SealedMonsterBlockEntity::new, SEALED_MONSTER.get()).build(null));

    private BuriedRegistries() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }
}
