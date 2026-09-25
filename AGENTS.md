# Buried Encounters

## Project identity

- Repository and artifact: `buried-encounters`
- Mod ID and namespace: `buried_encounters`
- Base package: `com.bettercontent.buriedencounters`
- Java 17, Minecraft Forge 1.20.1 / 47.4.13

## Scope

This mod adds rare, large Overworld buried TNT encounter sites. It has no dependency on
Explosion Overhaul or optional monster mods. Keep optional entity IDs guarded and inert
when their mods are absent.

## Validation

- Run `./gradlew verifyFast` for unit tests and the reobfuscated runtime JAR.
- Run `./gradlew verifyFull` for Forge GameTests.
- Keep generated Gradle state, build output, runtime worlds, logs, and IDE files untracked.

## Commit discipline

Commit coherent changes after required validation passes. Push only when a canonical
remote exists.
