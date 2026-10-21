package com.bettercontent.betterburiedencounters.diagnostics;

/** Pure observation of the owning feature's existing outcome, with no native reads. */
public final class FeatureTelemetry {
    private FeatureTelemetry() {}
    public static void release(PlaytestLog log, PlaytestLog.Context context, boolean inserted) {
        log.debug("release", "sealed_monster.release", context, inserted ? "inserted" : "rejected", inserted ? "native_insertion" : "native_insertion_denied", null);
    }
    public static void removal(PlaytestLog log, PlaytestLog.Context context, String result, String reason) {
        log.debug("removal", "sealed_monster.removal_watch", context, result, reason, null);
    }
}
