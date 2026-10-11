# Better Buried Encounters

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
