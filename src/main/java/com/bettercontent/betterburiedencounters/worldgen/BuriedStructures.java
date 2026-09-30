package com.bettercontent.betterburiedencounters.worldgen;

import com.bettercontent.betterburiedencounters.BuriedEncounters;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class BuriedStructures {
    public static final DeferredRegister<StructureType<?>> TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, BuriedEncounters.MOD_ID);
    public static final DeferredRegister<StructurePieceType> PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, BuriedEncounters.MOD_ID);

    public static final RegistryObject<StructureType<BuriedEncounterStructure>> BURIED_ENCOUNTER =
            TYPES.register("buried_encounter", () -> () -> BuriedEncounterStructure.CODEC);
    public static final RegistryObject<StructurePieceType> BURIED_ENCOUNTER_PIECE =
            PIECES.register("buried_encounter", () -> BuriedEncounterPiece::new);

    private BuriedStructures() {
    }

    public static void register(IEventBus modBus) {
        TYPES.register(modBus);
        PIECES.register(modBus);
    }
}
