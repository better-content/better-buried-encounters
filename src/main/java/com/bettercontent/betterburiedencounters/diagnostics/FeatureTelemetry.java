package com.bettercontent.betterburiedencounters.diagnostics;

/** Pure observation of the owning feature's existing outcome, with no native reads. */
public final class FeatureTelemetry {
    private FeatureTelemetry() {}
    public static void release(PlaytestLog log, boolean inserted) {
        log.event("release", "sealed_monster.release", "server", inserted ? "inserted" : "rejected", inserted ? "native_insertion" : "native_insertion_denied", null);
    }
}
