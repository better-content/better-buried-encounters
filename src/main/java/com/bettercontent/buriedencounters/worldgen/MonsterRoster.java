package com.bettercontent.buriedencounters.worldgen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;

public final class MonsterRoster {
    private static final List<ResourceLocation> UNDEAD = ids(
            "born_in_chaos_v1:decrepit_skeleton",
            "born_in_chaos_v1:skeleton_thrasher",
            "born_in_chaos_v1:zombie_bruiser",
            "born_in_chaos_v1:zombie_lumberjack",
            "born_in_chaos_v1:barrel_zombie",
            "born_in_chaos_v1:dread_hound",
            "minecraft:zombie",
            "minecraft:skeleton");
    private static final List<ResourceLocation> BURROWERS = ids(
            "alexscaves:gum_worm",
            "minecraft:silverfish",
            "minecraft:endermite",
            "minecraft:cave_spider");

    private MonsterRoster() {
    }

    public static List<ResourceLocation> available(boolean burrowerFamily) {
        List<ResourceLocation> preferred = burrowerFamily ? BURROWERS : UNDEAD;
        List<ResourceLocation> available = preferred.stream()
                .filter(id -> BuiltInRegistries.ENTITY_TYPE.getOptional(id)
                        .map(type -> type.getCategory() == MobCategory.MONSTER)
                        .orElse(false))
                .toList();
        if (!available.isEmpty()) return available;
        return List.of(new ResourceLocation("minecraft", burrowerFamily ? "silverfish" : "zombie"));
    }

    private static List<ResourceLocation> ids(String... values) {
        List<ResourceLocation> result = new ArrayList<>(values.length);
        for (String value : values) {
            ResourceLocation id = ResourceLocation.tryParse(value);
            if (id != null) result.add(id);
        }
        return List.copyOf(result);
    }
}
