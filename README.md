# Hunger Strike

Restores pre-hunger health mechanics to Minecraft. The hunger bar stops mattering: it sits pinned at
a configurable baseline, and eating food heals you directly instead of filling the bar — the way
health worked before Beta 1.8.

One repository, two jars: **NeoForge** and **Fabric**, both for **Minecraft 26.2**. The original mod
is by Texelsaur (jaquadro).

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

The Fabric build requires [Fabric API](https://modrinth.com/mod/fabric-api). The NeoForge build has
no dependencies beyond NeoForge itself.

## Configuration

`config/hungerstrike-common.toml`:

| Option | Default | Meaning |
| --- | --- | --- |
| `mode` | `ALL` | `NONE`, `LIST`, or `ALL`. See below. |
| `foodHealFactor` | `0.5` | Hearts gained per food point eaten. |
| `maxFoodStackSize` | `-1` | Global stack-size override for food items. `-1` keeps vanilla sizes. |
| `hideHungerBar` | `true` | Hide the food bar for affected players. |
| `hungerBaseline` | `10` | Where the bar is pinned, `1`–`20`. |

Both jars read and write the same file in the same place, byte for byte, so a config moves freely
between them and from the older Forge builds.

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

## Repository layout

```
common/          shared sources — not a Gradle project, see below
neoforge/        NeoForge module (ModDevGradle)
fabric/          Fabric module (Loom)
```

`common/` is a plain source directory, not a subproject. Each loader module adds it to its own
source set:

```groovy
sourceSets.main.java.srcDir rootProject.file('common/src/main/java')
sourceSets.main.resources.srcDir rootProject.file('common/src/main/resources')
```

So the shared classes are compiled twice — once against NeoForge's patched Minecraft, once against
Loom's — and land in both jars. There is no shared artifact to publish, no cross-plugin classpath to
reconcile, and no Architectury.

That works here because **Minecraft ships unobfuscated from 26.x on**: Fabric meta serves
intermediary `0.0.0`, Yarn stopped at 1.21.11, and both loaders now compile against official Mojang
names. `common/` needs no mapping shim and no `@ExpectPlatform` code generation.

The little that genuinely differs sits behind [`HungerStrikePlatform`](common/src/main/java/com/jaquadro/minecraft/hungerstrike/HungerStrikePlatform.java) —
the config directory, per-player attachment storage, and the mode-sync send. Each module installs
its implementation from its own entrypoint.

## Building

Requires JDK 25.

```
./gradlew build          # builds both loaders
./gradlew collectJars    # …and gathers both jars into build/libs
./gradlew :neoforge:build
./gradlew :fabric:build
```

| | jar |
| --- | --- |
| NeoForge | `neoforge/build/libs/HungerStrike-neoforge-26.2-9.0.0.jar` |
| Fabric | `fabric/build/libs/HungerStrike-fabric-26.2-9.0.0.jar` |

`collectJars` copies both into `build/libs/` at the root, which is what CI uploads.

Dev instances run per module: `./gradlew :neoforge:runClient`, `./gradlew :fabric:runServer`, and so
on.

## How the two modules differ

| NeoForge | Fabric |
| --- | --- |
| `@Mod` constructor + `IEventBus` | `ModInitializer` / `ClientModInitializer` in `fabric.mod.json` |
| `DeferredRegister` for attachment types | `AttachmentRegistry.create` at class init |
| `AttachmentType.builder().serialize(…).sync(…)` | `.persistent(Codec)` + `.syncWith(codec, AttachmentSyncPredicate.targetOnly())` |
| `PlayerTickEvent.Pre` / `Post` | a mixin at `HEAD`/`RETURN` of `Player#tick` — the same two points NeoForge fires those events from |
| tick-start food level in a non-serialized attachment | the same value in a `@Unique` mixin field |
| `ModifyDefaultComponentsEvent` | `DefaultItemComponentEvents.MODIFY` |
| `RenderGuiLayerEvent.Pre` cancel on `VanillaGuiLayers.FOOD_LEVEL` | `HudElementRegistry.replaceElement(VanillaHudElements.FOOD_BAR, …)` wrapping the delegate |
| `PayloadRegistrar#optional` + `PacketDistributor` | `PayloadTypeRegistry.clientboundPlay()` + `ServerPlayNetworking.canSend`/`send` |
| `RegisterCommandsEvent` | `CommandRegistrationCallback.EVENT` |
| `PlayerEvent.PlayerLoggedInEvent` | `ServerPlayConnectionEvents.JOIN` |
| `ClientPlayerNetworkEvent.LoggingOut` | `ClientPlayConnectionEvents.DISCONNECT` |
| `FMLPaths.CONFIGDIR` | `FabricLoader#getConfigDir` |

Everything else — the tick logic, the config, the command tree, the payload — is one copy in
`common/`.

## License

MIT. See [LICENSE](LICENSE).
