package com.bettercontent.buriedencounters.worldgen;

import com.bettercontent.buriedencounters.block.SealedMonsterBlock;
import com.bettercontent.buriedencounters.blockentity.SealedMonsterBlockEntity;
import com.bettercontent.buriedencounters.registry.BuriedRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public final class BuriedEncounterPiece extends StructurePiece {
    private static final int MAX_RADIUS = 32;
    private final BlockPos center;
    private final boolean explosionOverhaul;

    public BuriedEncounterPiece(BlockPos center, boolean explosionOverhaul) {
        super(BuriedStructures.BURIED_ENCOUNTER_PIECE.get(), 0,
                new BoundingBox(center.getX() - MAX_RADIUS, -64, center.getZ() - MAX_RADIUS,
                        center.getX() + MAX_RADIUS, 320, center.getZ() + MAX_RADIUS));
        this.center = center.immutable();
        this.explosionOverhaul = explosionOverhaul;
    }

    public BuriedEncounterPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(new BlockPos(tag.getInt("cx"), tag.getInt("cy"), tag.getInt("cz")), tag.getBoolean("eo"));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("cx", center.getX());
        tag.putInt("cy", center.getY());
        tag.putInt("cz", center.getZ());
        tag.putBoolean("eo", explosionOverhaul);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator,
                            RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {
        FieldProfile profile = FieldProfile.forExplosionOverhaul(explosionOverhaul);
        for (int x = center.getX() - profile.radius(); x <= center.getX() + profile.radius(); x++) {
            for (int z = center.getZ() - profile.radius(); z <= center.getZ() + profile.radius(); z++) {
                if (x < box.minX() || x > box.maxX() || z < box.minZ() || z > box.maxZ()) continue;
                if (x == center.getX() && z == center.getZ()) continue;
                int ground = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
                if (ground < level.getMinBuildHeight() + 4) continue;
                decorateSurface(level, box, x, z, ground);
            }
        }
        placeTntPile(level, box);
        for (BuriedMonsterSpot spot : FieldLayout.create(center.getX(), center.getZ(), profile.radius(), 0xB017EDL)) {
            int ground = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, spot.x(), spot.z()) - 1;
            BlockPos monsterPos = new BlockPos(spot.x(), ground - 1, spot.z());
            if (!box.isInside(monsterPos) || monsterPos.getY() <= level.getMinBuildHeight()) continue;
            int palette = Math.floorMod(spot.entityId().hashCode(), 3);
            BlockState state = BuriedRegistries.SEALED_MONSTER.get().defaultBlockState()
                    .setValue(SealedMonsterBlock.PALETTE, palette);
            if (level.setBlock(monsterPos, state, 2)
                    && level.getBlockEntity(monsterPos) instanceof SealedMonsterBlockEntity sealed) {
                sealed.setEntityId(spot.entityId());
            }
        }
    }

    private void placeTntPile(WorldGenLevel level, BoundingBox box) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                int ground = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
                BlockPos lower = new BlockPos(x, ground - 1, z);
                BlockPos upper = new BlockPos(x, ground, z);
                if (box.isInside(lower)) level.setBlock(lower, Blocks.TNT.defaultBlockState(), 2);
                if (box.isInside(upper)) level.setBlock(upper, Blocks.TNT.defaultBlockState(), 2);
            }
        }
    }

    private void decorateSurface(WorldGenLevel level, BoundingBox box, int x, int z, int ground) {
        int dx = x - center.getX();
        int dz = z - center.getZ();
        int distanceSquared = dx * dx + dz * dz;
        if (distanceSquared < 4 || distanceSquared > 30) return;
        BlockState decoration = null;
        if (Math.floorMod(dx * 31 + dz * 17, 19) == 0) decoration = Blocks.GRAVEL.defaultBlockState();
        else if (Math.floorMod(dx * 11 - dz * 23, 29) == 0) decoration = Blocks.COARSE_DIRT.defaultBlockState();
        else if ((Math.abs(dx) == 4 && Math.abs(dz) == 2) || (Math.abs(dx) == 2 && Math.abs(dz) == 4)) {
            decoration = Blocks.BONE_BLOCK.defaultBlockState();
        } else if (Math.floorMod(dx * 7 + dz * 13, 37) == 0) {
            decoration = Blocks.COBBLESTONE.defaultBlockState();
        }
        if (decoration == null) return;
        BlockPos pos = new BlockPos(x, ground, z);
        if (box.isInside(pos) && level.getBlockState(pos).isSolid()) {
            level.setBlock(pos, decoration, 2);
        }
    }
}
