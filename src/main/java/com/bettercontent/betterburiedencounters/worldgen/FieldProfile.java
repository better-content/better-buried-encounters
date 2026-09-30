package com.bettercontent.betterburiedencounters.worldgen;

import net.minecraftforge.fml.ModList;

public enum FieldProfile {
    VANILLA(16),
    EXPLOSION_OVERHAUL(32);

    private final int radius;

    FieldProfile(int radius) {
        this.radius = radius;
    }

    public int radius() {
        return radius;
    }

    public static FieldProfile forExplosionOverhaul(boolean loaded) {
        return loaded ? EXPLOSION_OVERHAUL : VANILLA;
    }

    public static FieldProfile current() {
        return forExplosionOverhaul(ModList.get().isLoaded("explosionoverhaul"));
    }
}
