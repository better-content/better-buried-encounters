package com.bettercontent.buriedencounters;

import com.bettercontent.buriedencounters.block.SealedMonsterBlock;
import com.bettercontent.buriedencounters.blockentity.SealedMonsterBlockEntity;
import com.bettercontent.buriedencounters.worldgen.ExplosionRemovalTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class BuriedEncountersEvents {
    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        for (var pos : event.getAffectedBlocks()) {
            if (level.getBlockState(pos).getBlock() instanceof SealedMonsterBlock
                    && level.getBlockEntity(pos) instanceof SealedMonsterBlockEntity sealed) {
                ExplosionRemovalTracker.watch(level, pos, sealed.entityId());
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (ServerLevel level : event.getServer().getAllLevels()) {
            ExplosionRemovalTracker.tick(level);
        }
    }

    @SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            ExplosionRemovalTracker.forget(level);
        }
    }
}
