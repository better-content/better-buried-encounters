package com.bettercontent.betterburiedencounters.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

public final class MonsterSpawner {
    private MonsterSpawner() {
    }

    public static void spawn(Level level, BlockPos pos, ResourceLocation entityId) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        var type = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId).orElse(null);
        if (type == null) {
            outcome(serverLevel, pos, null, "rejected", "optional_type_absent");
            return;
        }
        Entity created = type.create(serverLevel);
        if (!(created instanceof Mob mob)) {
            outcome(serverLevel, pos, created, "rejected", "not_mob");
            return;
        }
        BlockPos spawnPos = findOpenPosition(serverLevel, mob, pos);
        mob.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                serverLevel.random.nextFloat() * 360.0F, 0.0F);
        mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos),
                MobSpawnType.TRIGGERED, null, null);
        boolean inserted = serverLevel.addFreshEntity(mob);
        var log = com.bettercontent.betterburiedencounters.PlaytestDiagnostics.LOG;
        if (log.enabled("release")) com.bettercontent.betterburiedencounters.diagnostics.FeatureTelemetry.release(log,
                context(serverLevel, pos, mob), inserted);
    }

    private static com.bettercontent.betterburiedencounters.diagnostics.PlaytestLog.Context context(ServerLevel level, BlockPos origin, Entity created) {
        return new com.bettercontent.betterburiedencounters.diagnostics.PlaytestLog.Context("server",
                level.dimension().location().toString(), level.getGameTime(), created == null ? "" : created.getStringUUID(),
                origin.getX() + "," + origin.getY() + "," + origin.getZ());
    }

    private static void outcome(ServerLevel level, BlockPos origin, Entity created, String result, String reason) {
        var log = com.bettercontent.betterburiedencounters.PlaytestDiagnostics.LOG;
        if (log.enabled("release")) log.debug("release", "sealed_monster.release",
                context(level, origin, created),
                result, reason, null);
    }

    private static BlockPos findOpenPosition(ServerLevel level, Mob mob, BlockPos origin) {
        for (int dy = 0; dy <= 8; dy++) {
            BlockPos candidate = origin.above(dy);
            if (isOpenPosition(level, mob, candidate)) return candidate;
        }

        for (int dy = 0; dy <= 8; dy++) {
            for (int radius = 1; radius <= 2; radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                        BlockPos candidate = origin.offset(dx, dy, dz);
                        if (isOpenPosition(level, mob, candidate)) return candidate;
                    }
                }
            }
        }
        return origin;
    }

    private static boolean isOpenPosition(ServerLevel level, Mob mob, BlockPos candidate) {
        if (candidate.getY() >= level.getMaxBuildHeight()) return false;
        mob.moveTo(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5, 0.0F, 0.0F);
        return level.noCollision(mob);
    }
}
