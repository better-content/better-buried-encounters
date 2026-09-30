package com.bettercontent.betterburiedencounters.worldgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;

public final class FieldLayout {
    public static final int MONSTER_BLOCK_COUNT = 32;
    private static final int CORE_EXCLUSION_RADIUS = 5;
    private static final double MINIMUM_SPACING_SQUARED = 9.0;

    private FieldLayout() {
    }

    public static List<BuriedMonsterSpot> create(int centerX, int centerZ, int radius, long seed) {
        Random random = new Random(seed ^ ((long) centerX * 341873128712L)
                ^ ((long) centerZ * 132897987541L));
        List<BuriedMonsterSpot> result = new ArrayList<>(MONSTER_BLOCK_COUNT);
        for (FieldOffset offset : offsets(centerX, centerZ, radius, seed)) {
            boolean burrower = random.nextInt(100) < 35;
            List<ResourceLocation> roster = MonsterRoster.available(burrower);
            ResourceLocation entityId = roster.get(random.nextInt(roster.size()));
            result.add(new BuriedMonsterSpot(offset.x(), offset.z(), entityId));
        }
        return List.copyOf(result);
    }

    public static List<FieldOffset> offsets(int centerX, int centerZ, int radius, long seed) {
        Random random = new Random(seed ^ ((long) centerX * 341873128712L)
                ^ ((long) centerZ * 132897987541L));
        List<FieldOffset> candidates = new ArrayList<>();
        int radiusSquared = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distanceSquared = x * x + z * z;
                if (distanceSquared <= radiusSquared && distanceSquared >= CORE_EXCLUSION_RADIUS * CORE_EXCLUSION_RADIUS) {
                    candidates.add(new FieldOffset(x, z));
                }
            }
        }
        Collections.shuffle(candidates, random);

        List<FieldOffset> selected = new ArrayList<>(MONSTER_BLOCK_COUNT);
        for (FieldOffset candidate : candidates) {
            boolean separated = selected.stream().allMatch(other -> {
                int dx = candidate.x() - other.x();
                int dz = candidate.z() - other.z();
                return dx * dx + dz * dz >= MINIMUM_SPACING_SQUARED;
            });
            if (separated) selected.add(candidate);
            if (selected.size() == MONSTER_BLOCK_COUNT) break;
        }
        for (FieldOffset candidate : candidates) {
            if (selected.size() == MONSTER_BLOCK_COUNT) break;
            if (!selected.contains(candidate)) selected.add(candidate);
        }

        return selected.stream()
                .map(offset -> new FieldOffset(centerX + offset.x(), centerZ + offset.z()))
                .toList();
    }

    public record FieldOffset(int x, int z) {
    }
}
