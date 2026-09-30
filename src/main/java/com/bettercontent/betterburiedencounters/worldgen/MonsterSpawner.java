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
        if (type == null) return;
        Entity created = type.create(serverLevel);
        if (!(created instanceof Mob mob)) return;
        BlockPos spawnPos = findOpenPosition(serverLevel, mob, pos);
        mob.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                serverLevel.random.nextFloat() * 360.0F, 0.0F);
        mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos),
                MobSpawnType.TRIGGERED, null, null);
        serverLevel.addFreshEntity(mob);
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
