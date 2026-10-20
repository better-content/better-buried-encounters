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
        FeatureTelemetry.release(log, false);
        if (records.size() != 1 || !records.get(0).contains("result=rejected reason=native_insertion_denied")
                || records.get(0).contains("subject=") || records.get(0).contains("pos=")
                || records.get(0).contains("player=") || records.get(0).contains("payload=")) {
            throw new AssertionError(records.toString());
        }
    }
    public static void main(String[] args) { new FeatureTelemetryTest().nativeOutcomeIsNotMisreportedOrExpandedIntoPrivatePayload(); }
}
