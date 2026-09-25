package com.bettercontent.buriedencounters.worldgen;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public final class BuriedEncounterStructure extends Structure {
    public static final Codec<BuriedEncounterStructure> CODEC = simpleCodec(BuriedEncounterStructure::new);

    public BuriedEncounterStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunk = context.chunkPos();
        int x = chunk.getMiddleBlockX();
        int z = chunk.getMiddleBlockZ();
        int floor = context.chunkGenerator().getFirstFreeHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG,
                context.heightAccessor(), context.randomState());
        BlockPos center = new BlockPos(x, floor, z);
        boolean overhaul = FieldProfile.current() == FieldProfile.EXPLOSION_OVERHAUL;
        return Optional.of(new GenerationStub(center,
                pieces -> pieces.addPiece(new BuriedEncounterPiece(center, overhaul))));
    }

    @Override
    public StructureType<?> type() {
        return BuriedStructures.BURIED_ENCOUNTER.get();
    }
}
