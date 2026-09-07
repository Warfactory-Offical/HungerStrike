# Hunger Strike

Restores pre-hunger health mechanics to Minecraft. The hunger bar stops mattering: it sits pinned at
a configurable baseline, and eating food heals you directly instead of filling the bar — the way
health worked before Beta 1.8.

This is a NeoForge port for **Minecraft 26.2**. The original mod is by Texelsaur (jaquadro).

## What it does

- **Hunger never drains.** The bar is held at `hungerBaseline` (default 10 — half full, which
  disables passive regen but still allows sprinting).
- **Food heals.** Nutrition you would have gained is paid out as hearts instead, at
  `foodHealFactor` hearts per food point (default 0.5).
- **The bar is hidden** for affected players, since it no longer conveys anything.
- **Hunger and Regeneration still work.** Hunger drops the bar below the sprint threshold;
  Regeneration fills it so vanilla regen kicks in.
- **Per-player or server-wide.** Run it for everyone, for nobody, or only for players an operator
  has added to the list.

## Configuration

`config/hungerstrike-common.toml`:

| Option | Default | Meaning |
| --- | --- | --- |
| `mode` | `ALL` | `NONE`, `LIST`, or `ALL`. See below. |
| `foodHealFactor` | `0.5` | Hearts gained per food point eaten. |
| `maxFoodStackSize` | `-1` | Global stack-size override for food items. `-1` keeps vanilla sizes. |
| `hideHungerBar` | `true` | Hide the food bar for affected players. |
| `hungerBaseline` | `10` | Where the bar is pinned, `1`–`20`. |

Config files from the 1.20.1 Forge build carry over as-is.

### Modes

- `NONE` — disabled for everyone.
- `LIST` — enabled only for players added with `/hungerstrike add`.
- `ALL` — enabled for everyone.

## Commands

Requires permission level 3 (gamemaster).

```
/hungerstrike list                 # players currently on strike
/hungerstrike add <targets>        # put players on strike (LIST mode)
/hungerstrike remove <targets>     # take players off strike
/hungerstrike mode                 # report the active mode
/hungerstrike setmode <none|list|all>
```

`setmode` writes to the config and pushes the new mode to every connected client.

## Building

Requires JDK 25.

```
./gradlew build
```

The jar lands in `build/libs/`. `./gradlew runClient` and `./gradlew runServer` start a dev instance.

## Porting notes

The 1.20.1 Forge version relied on several APIs that no longer exist. What changed:

| Then (Forge, 1.20.1) | Now (NeoForge, 26.2) |
| --- | --- |
| `ExtendedPlayer` capability + `ExtendedPlayerProvider` | A synced, serialized [data attachment](src/main/java/com/jaquadro/minecraft/hungerstrike/HungerStrikeAttachments.java) |
| `PlayerHandler` map keyed by `GameProfile`, to carry strike state through death | `AttachmentType.Builder#copyOnDeath` |
| `PacketSyncExtendedPlayer` + `PacketRequestSync` | Deleted — attachment sync covers both, including the initial push on join |
| `SimpleChannel` + `registerMessage` | `CustomPacketPayload` + `PayloadRegistrar`, for the one remaining packet (mode sync) |
| Access transformer on `Item.maxStackSize` | `ModifyDefaultComponentsEvent` — stack size is a data component since 1.20.5 |
| `ClientProxy`/`ServerProxy` via `DistExecutor` | `@Mod(dist = Dist.CLIENT)` client entrypoint |
| `RenderGuiOverlayEvent` + `VanillaGuiOverlay` | `RenderGuiLayerEvent` + `VanillaGuiLayers` |
| `TickEvent.PlayerTickEvent` with a `Phase` field | `PlayerTickEvent.Pre` / `PlayerTickEvent.Post` |
| `FoodData#eat` arithmetic to force a food level | `FoodData#setFoodLevel` / `#setSaturation`, both public now |
| `ForgeConfigSpec` | `ModConfigSpec` |
| `ResourceLocation` | `Identifier` |
| `Collection<GameProfile>` from `GameProfileArgument` | `Collection<NameAndId>` |
| `requires(src -> src.hasPermission(3))` | `requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))` |

One behavioural addition: the client now distinguishes the mode from its own config file from the
mode the server pushed, and drops the server's value on disconnect. The old build wrote the server's
mode straight into the client's config object, which could leak between worlds.

## License

MIT. See [LICENSE](LICENSE).
