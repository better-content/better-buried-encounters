package com.bettercontent.betterburiedencounters.worldgen;

import com.bettercontent.betterburiedencounters.registry.BuriedRegistries;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

/** Bridges Explosion Overhaul's optional direct chunk writes, which bypass block removal callbacks. */
public final class ExplosionRemovalTracker {
    private static final int MAX_WAIT_TICKS = 1200;
    private static final Map<ServerLevel, Map<BlockPos, PendingSpawn>> PENDING = new WeakHashMap<>();

    private ExplosionRemovalTracker() {
    }

    public static void watch(ServerLevel level, BlockPos pos, ResourceLocation entityId) {
        if (entityId == null) return;
        boolean admitted = PENDING.computeIfAbsent(level, ignored -> new HashMap<>())
                .putIfAbsent(pos.immutable(), new PendingSpawn(entityId, level.getServer().getTickCount())) == null;
        if (admitted) outcome(level, pos, "admitted", "watch_registered");
    }

    public static boolean consume(net.minecraft.world.level.Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        Map<BlockPos, PendingSpawn> pending = PENDING.get(serverLevel);
        if (pending == null) return false;
        boolean removed = pending.remove(pos) != null;
        if (removed) outcome(serverLevel, pos, "consumed", "native_callback");
        if (pending.isEmpty()) PENDING.remove(serverLevel);
        return removed;
    }

    public static void tick(ServerLevel level) {
        Map<BlockPos, PendingSpawn> pending = PENDING.get(level);
        if (pending == null || pending.isEmpty()) return;
        int tick = level.getServer().getTickCount();
        var iterator = pending.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            BlockPos pos = entry.getKey();
            PendingSpawn spawn = entry.getValue();
            if (tick - spawn.startedAt() > MAX_WAIT_TICKS) {
                outcome(level, pos, "expired", "timeout");
                iterator.remove();
            } else if (level.hasChunkAt(pos)) {
                BlockState state = level.getBlockState(pos);
                if (!state.is(BuriedRegistries.SEALED_MONSTER.get())) {
                    MonsterSpawner.spawn(level, pos, spawn.entityId());
                    outcome(level, pos, "dispatched", "observed_block_removal");
                    iterator.remove();
                }
            }
        }
        if (pending.isEmpty()) PENDING.remove(level);
    }

    public static void forget(ServerLevel level) {
        if (PENDING.remove(level) != null) outcome(level, null, "cancelled", "level_unload");
    }

    private static void outcome(ServerLevel level, BlockPos origin, String result, String reason) {
        var log = com.bettercontent.betterburiedencounters.PlaytestDiagnostics.LOG;
        if (log.enabled("removal")) com.bettercontent.betterburiedencounters.diagnostics.FeatureTelemetry.removal(log,
                new com.bettercontent.betterburiedencounters.diagnostics.PlaytestLog.Context("server",
                        level.dimension().location().toString(), level.getGameTime(), "",
                        origin == null ? "" : origin.getX() + "," + origin.getY() + "," + origin.getZ()), result, reason);
    }

    private record PendingSpawn(ResourceLocation entityId, int startedAt) {
    }
}
