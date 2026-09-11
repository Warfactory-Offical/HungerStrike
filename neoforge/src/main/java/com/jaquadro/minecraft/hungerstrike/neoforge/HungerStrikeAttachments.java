package com.jaquadro.minecraft.hungerstrike.neoforge;

import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.jaquadro.minecraft.hungerstrike.HungerStrikeHandler;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Replaces the {@code ExtendedPlayer} capability the Forge builds used. A data attachment gives us
 * persistence, copy-on-death and client sync for free, which is why the sync/request packets and
 * the {@code PlayerHandler} death-data cache from the old versions are gone.
 */
public final class HungerStrikeAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, HungerStrike.MOD_ID);

    /**
     * Whether this player is on hunger strike. Only meaningful in
     * {@link com.jaquadro.minecraft.hungerstrike.Config.Mode#LIST}. Synced so the client can hide
     * its own hunger bar without asking the server.
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> ON_STRIKE =
            ATTACHMENT_TYPES.register("on_strike", () -> AttachmentType
                    .builder(() -> Boolean.FALSE)
                    .serialize(Codec.BOOL.fieldOf("enabled"))
                    .copyOnDeath()
                    .sync(ByteBufCodecs.BOOL)
                    .build());

    /**
     * Food level recorded at the start of the player's current tick, or
     * {@link HungerStrikeHandler#NO_TICK_START} when the mod is not active for them.
     *
     * <p>Scratch space with the lifetime of one {@code Player#tick}, so it is deliberately neither
     * serialized, synced nor copied on death. Holding it per player rather than in a map on the
     * event handler is what keeps the client and server player objects of an integrated server off
     * the same mutable state. The Fabric module reaches the same end with a {@code @Unique} mixin
     * field.
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> TICK_START_FOOD_LEVEL =
            ATTACHMENT_TYPES.register("tick_start_food_level", () -> AttachmentType
                    .builder(() -> HungerStrikeHandler.NO_TICK_START)
                    .build());

    private HungerStrikeAttachments() {}
}
