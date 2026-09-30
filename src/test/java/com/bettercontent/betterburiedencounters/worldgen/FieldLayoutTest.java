package com.bettercontent.betterburiedencounters.worldgen;

import java.util.HashSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldLayoutTest {
    @Test
    void vanillaAndOverhaulProfilesHaveTheirAuthoredRadii() {
        assertEquals(16, FieldProfile.forExplosionOverhaul(false).radius());
        assertEquals(32, FieldProfile.forExplosionOverhaul(true).radius());
    }

    @Test
    void largeFieldContainsThirtyTwoStableDistinctSpotsOutsideTheTntCore() {
        var spots = FieldLayout.offsets(128, -96, 32, 42L);
        assertEquals(32, spots.size());
        assertEquals(spots, FieldLayout.offsets(128, -96, 32, 42L));
        assertEquals(32, new HashSet<>(spots).size());
        for (var spot : spots) {
            int dx = spot.x() - 128;
            int dz = spot.z() + 96;
            assertTrue(dx * dx + dz * dz <= 32 * 32);
            assertTrue(dx * dx + dz * dz >= 25);
        }
        assertNotEquals(spots, FieldLayout.offsets(128, -96, 32, 43L));
    }

    @Test
    void vanillaFootprintAlsoFitsAllThirtyTwoSpots() {
        assertEquals(32, FieldLayout.offsets(0, 0, 16, 901L).size());
    }
}
