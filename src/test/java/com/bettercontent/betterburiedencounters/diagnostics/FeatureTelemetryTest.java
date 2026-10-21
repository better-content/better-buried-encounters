package com.bettercontent.betterburiedencounters.diagnostics;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class FeatureTelemetryTest {
    @Test public void nativeOutcomeIsNotMisreportedOrExpandedIntoPrivatePayload() {
        List<String> records = new ArrayList<>();
        PlaytestLog log = new PlaytestLog("better_buried_encounters", new PlaytestLog.Sink() {
            public boolean enabled(String category, String level) { return true; }
            public void write(String category, String level, String message) { records.add(message); }
        }, () -> 0L, new Properties());
        var origin = new PlaytestLog.Context("server", "minecraft:overworld", 42, "", "10,64,-3");
        var created = new PlaytestLog.Context("server", "minecraft:overworld", 43,
                "7ac1c622-f1a4-4986-9404-70bf2345061c", "10,64,-3");
        FeatureTelemetry.removal(log, origin, "admitted", "watch_registered");
        FeatureTelemetry.release(log, created, false);
        if (records.size() != 2 || !records.get(0).contains("result=admitted reason=watch_registered")
                || !records.get(1).contains("result=rejected reason=native_insertion_denied")
                || !records.get(1).contains("subject=7ac1c622-f1a4-4986-9404-70bf2345061c")
                || !records.get(0).contains("tick=42") || !records.get(1).contains("tick=43")) {
            throw new AssertionError(records.toString());
        }
        for (String record : records) {
            if (!record.contains("dimension=minecraft:overworld") || !record.contains("pos=10,64,-3")
                    || record.contains("player=") || record.contains("payload=") || record.contains("seed="))
                throw new AssertionError(record);
        }
    }
    public static void main(String[] args) { new FeatureTelemetryTest().nativeOutcomeIsNotMisreportedOrExpandedIntoPrivatePayload(); }
}
