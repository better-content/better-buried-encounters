# Better Buried Encounters

## Native fixture verification

`verifyFast` checks absent-only native settings preparation, preservation of existing settings,
and synthetic ERROR/FATAL rejection. `verifyFull` prepares defaults-only `server.properties` in
ForgeGradle's actual native RunConfig directory and rejects actual native ERROR/FATAL.

## Playtest logging

`bc.playtest.better_buried_encounters.release` distinguishes absent optional types, non-Mob
creation and actual native insertion; `.removal` observes native callback consumption, asynchronous
removal dispatch, timeout and unload cancellation. Admission/consume/expire/dispatch and release
share the native dimension, game tick and sealed-block source position. A created entity's native
UUID links its insertion result to that source; rejection before creation has no invented UUID.
These bounded causal origins/UUIDs are intentional, not a per-block worldgen stream. Unload-only
cancellation has level context without an extra position traversal. Dispatch never claims successful
spawning. No player names, entity payloads or seeds are logged. DEBUG/TRACE share 120/minute (burst 20),
256 aggregation keys and 2,048-character records, with 30-second coalesced summaries. Startup INFO
reports version/mode/limits and Explosion Overhaul presence; unavailable source identity is
`UNKNOWN`. Stop/disconnect clears transient diagnostics without rearming process allowances.
Pure emitter/contracts supplement the existing sealed-block/asynchronous-removal native gates.

## Scope and authority

This repository owns its mod-specific behavior and authoring inputs. Read [local instructions](AGENTS.md)
and the [shared documentation/policy index](../../better-content-modpack/docs/README.md).


Better Buried Encounters adds rare, large surface sites with most of their encounter hidden just
under the local ground. A half-buried TNT pile marks the center. Thirty-two sealed monster
blocks are scattered through the surrounding field; destroying one releases its stored mob.

The field has a radius of 16 blocks without Explosion Overhaul and 32 blocks when that mod is
loaded. The structure uses those two authored profiles and does not read server explosion
settings. It can generate in any Overworld biome. Small patches of rubble, bones, and disturbed
soil provide a surface trace without building a large ruin.

The mod uses optional mob IDs from the pack, including undead from Born in Chaos and the gum
worm from Alex's Caves. Missing entities are ignored; vanilla silverfish, cave spiders, and
undead provide fallbacks.

## Verification

Run `./gradlew verifyFast` for deterministic layout tests and a runtime JAR. Run
`./gradlew verifyFull` to execute the Forge GameTests for sealed-block destruction and the
Explosion Overhaul asynchronous removal path.
