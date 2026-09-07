# Changelog

## 9.0.0 — Minecraft 26.2

Port to NeoForge for Minecraft 26.2, from the 8.0.0 Forge build for 1.20.1.

### Changed

- Per-player strike state moved from a Forge capability to a NeoForge data attachment. It persists,
  survives death, and syncs to the owning client on its own.
- The `maxFoodStackSize` override now goes through `ModifyDefaultComponentsEvent` instead of an
  access transformer, since stack size became a data component in 1.20.5. It applies to every item
  carrying a food component rather than every subclass of the old `ItemFood`.
- Networking rebuilt on `CustomPacketPayload`. Only the mode-sync packet survives; the payload is
  registered as optional, so a client without the mod can still connect to a server that has it.
- Client behaviour split into a `dist = Dist.CLIENT` entrypoint, replacing the proxy classes.
- The hunger bar is pinned with `FoodData#setFoodLevel`/`#setSaturation` rather than by working
  backwards through `eat`.

### Fixed

- A mode pushed by a server no longer overwrites the client's own config value, and is discarded
  when the client disconnects. Previously a server's mode could persist into the next world joined.

### Unchanged

- Config options, their defaults, and the file name (`hungerstrike-common.toml`). Existing config
  files carry over.
- Command syntax and all translation keys.
