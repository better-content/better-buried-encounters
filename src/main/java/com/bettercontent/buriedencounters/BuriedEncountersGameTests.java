package com.bettercontent.buriedencounters;

import com.bettercontent.buriedencounters.blockentity.SealedMonsterBlockEntity;
import com.bettercontent.buriedencounters.registry.BuriedRegistries;
import com.bettercontent.buriedencounters.worldgen.ExplosionRemovalTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BuriedEncounters.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BuriedEncountersGameTests {
    private BuriedEncountersGameTests() {
    }

    public static void register(RegisterGameTestsEvent event) {
        event.register(BuriedEncountersGameTests.class);
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "buried_break", timeoutTicks = 40)
    public static void breakingOneSealedBlockSpawnsItsStoredMobOnce(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        putSealedMob(helper, pos, "zombie");
        helper.getLevel().setBlockAndUpdate(pos.above(), Blocks.STONE.defaultBlockState());
        helper.getLevel().destroyBlock(pos, false);
        helper.runAfterDelay(1, () -> {
            var mobs = mobsNear(helper.getLevel(), pos, Zombie.class);
            helper.assertTrue(mobs.size() == 1, "a broken sealed block must release exactly one stored mob");
            helper.assertTrue(mobs.get(0).blockPosition().getY() >= pos.getY() + 2,
                    "a tall mob must emerge above the one-block soil cover");
            mobs.forEach(net.minecraft.world.entity.Mob::discard);
            helper.succeed();
        });
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "buried_tnt", timeoutTicks = 40)
    public static void tntExplosionBreaksASealedBlockAndReleasesItsMob(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(4, 2, 2));
        putSealedMob(helper, pos, "cave_spider");
        helper.getLevel().explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                4.0F, Level.ExplosionInteraction.TNT);
        helper.assertTrue(!helper.getLevel().getBlockState(pos).is(BuriedRegistries.SEALED_MONSTER.get()),
                "the TNT fixture must actually destroy the sealed block");
        helper.runAfterDelay(1, () -> {
            int count = mobsNear(helper.getLevel(), pos, CaveSpider.class).size();
            helper.assertTrue(count == 1, "a TNT explosion must release one sealed mob; found " + count);
            mobsNear(helper.getLevel(), pos, CaveSpider.class).forEach(net.minecraft.world.entity.Mob::discard);
            helper.succeed();
        });
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "buried_async", timeoutTicks = 40)
    public static void deferredAsyncExplosionRemovalSpawnsExactlyOnce(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(3, 2, 3));
        putSealedMob(helper, pos, "zombie");
        ServerLevel level = helper.getLevel();
        ExplosionRemovalTracker.watch(level, pos, new net.minecraft.resources.ResourceLocation("minecraft", "zombie"));

        var chunk = level.getChunkAt(pos);
        var section = chunk.getSection(chunk.getSectionIndex(pos.getY()));
        section.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, Blocks.AIR.defaultBlockState(), false);
        helper.assertTrue(!level.getBlockState(pos).is(BuriedRegistries.SEALED_MONSTER.get()),
                "direct section write must remove the sealed block fixture");
        ExplosionRemovalTracker.tick(level);
        ExplosionRemovalTracker.tick(level);

        int count = mobsNear(level, pos, Zombie.class).size();
        helper.assertTrue(count == 1,
                "direct chunk removal must release one captured mob across repeated scans; found " + count);
        mobsNear(level, pos, Zombie.class).forEach(net.minecraft.world.entity.Mob::discard);
        helper.succeed();
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", batch = "buried_worldgen", timeoutTicks = 40)
    public static void buriedStructureAndRareStructureSetAreRegistered(GameTestHelper helper) {
        var registryAccess = helper.getLevel().registryAccess();
        var structures = registryAccess.registryOrThrow(Registries.STRUCTURE);
        var structureSets = registryAccess.registryOrThrow(Registries.STRUCTURE_SET);
        var structureId = new net.minecraft.resources.ResourceLocation(BuriedEncounters.MOD_ID, "buried_encounter");
        var structureSetId = new net.minecraft.resources.ResourceLocation(BuriedEncounters.MOD_ID, "buried_encounters");
        helper.assertTrue(structures.containsKey(structureId), "Buried Encounter structure data must load");
        helper.assertTrue(structureSets.containsKey(structureSetId), "rare Buried Encounter structure set must load");
        helper.succeed();
    }

    private static void putSealedMob(GameTestHelper helper, BlockPos pos, String path) {
        helper.getLevel().setBlockAndUpdate(pos, BuriedRegistries.SEALED_MONSTER.get().defaultBlockState());
        if (helper.getLevel().getBlockEntity(pos) instanceof SealedMonsterBlockEntity sealed) {
            sealed.setEntityId(new net.minecraft.resources.ResourceLocation("minecraft", path));
        }
    }

    private static <T extends net.minecraft.world.entity.Mob> java.util.List<T> mobsNear(
            ServerLevel level, BlockPos pos, Class<T> type) {
        return level.getEntitiesOfClass(type, new AABB(pos).inflate(4));
    }
}
