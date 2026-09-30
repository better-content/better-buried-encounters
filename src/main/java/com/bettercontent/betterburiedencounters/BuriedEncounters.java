package com.bettercontent.betterburiedencounters;

import com.bettercontent.betterburiedencounters.registry.BuriedRegistries;
import com.bettercontent.betterburiedencounters.worldgen.BuriedStructures;
import com.bettercontent.betterburiedencounters.BuriedEncountersGameTests;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BuriedEncounters.MOD_ID)
public final class BuriedEncounters {
    public static final String MOD_ID = "better_buried_encounters";

    public BuriedEncounters() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BuriedRegistries.register(modBus);
        BuriedStructures.register(modBus);
        modBus.addListener(BuriedEncountersGameTests::register);
        if (ForgeGameTestHooks.isGametestEnabled()) {
            GameTestRegistry.register(BuriedEncountersGameTests.class);
        }
        MinecraftForge.EVENT_BUS.register(new BuriedEncountersEvents());
    }
}
