# Changelog

## 9.0.0 — Minecraft 26.2

Port to Minecraft 26.2 from the 8.0.0 Forge build for 1.20.1, shipping as two jars — NeoForge and
Fabric — out of one repository.

### Added

- A Fabric build. `HungerStrike-fabric-26.2-<version>.jar` alongside
  `HungerStrike-neoforge-26.2-<version>.jar`; `./gradlew build` produces both, and
  `./gradlew collectJars` gathers them into the root `build/libs`.
- Multi-loader layout: `common/` holds the shared sources as a plain directory that each loader
  module adds to its own source set, so nothing has to be published or remapped between them. This
  works without Architectury because Minecraft ships unobfuscated from 26.x on, leaving both
  loaders compiling against official Mojang names.
- `HungerStrikePlatform`, the small bridge covering the three things that genuinely differ: the
  config directory, per-player attachment storage, and the mode-sync send.

### Changed

- Per-player strike state moved from a Forge capability to a NeoForge data attachment. It persists,
  survives death, and syncs to the owning client on its own.
- The `maxFoodStackSize` override now goes through `ModifyDefaultComponentsEvent` instead of an
  access transformer, since stack size became a data component in 1.20.5. It applies to every item
  carrying a food component rather than every subclass of the old `ItemFood`.
- Config handling moved off `ModConfigSpec` onto a small shared TOML reader/writer, so both loaders
  run one implementation. It reproduces the file `ModConfigSpec` emitted exactly; the cost is that
  the NeoForge build no longer live-reloads the file or exposes a generated config screen.
- Networking rebuilt on `CustomPacketPayload`. Only the mode-sync packet survives; the payload is
  registered as optional, so a client without the mod can still connect to a server that has it.
- Client behaviour split into a `dist = Dist.CLIENT` entrypoint, replacing the proxy classes.
- The hunger bar is pinned with `FoodData#setFoodLevel`/`#setSaturation` rather than by working
  backwards through `eat`.

### Fixed

- A mode pushed by a server no longer overwrites the client's own config value, and is discarded
  when the client disconnects. Previously a server's mode could persist into the next world joined.
- The food level recorded at the start of a tick is held on the player again, as a non-serialized
  data attachment, restoring what the 1.20.1 build kept in `ExtendedPlayer#startHunger`. An earlier
  draft of this port parked it in an `IdentityHashMap` on the single handler instance, which both
  the client and the server thread write to on an integrated server.

### Unchanged

- Config options, their defaults, and the file name (`hungerstrike-common.toml`). Existing config
  files carry over, and the two loaders write the file byte for byte identically, so one config
  serves either jar.
- Command syntax and all translation keys.
