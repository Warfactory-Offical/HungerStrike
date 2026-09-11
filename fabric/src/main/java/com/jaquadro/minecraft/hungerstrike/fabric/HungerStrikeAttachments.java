package com.jaquadro.minecraft.hungerstrike.fabric;

import com.jaquadro.minecraft.hungerstrike.Config;
import com.jaquadro.minecraft.hungerstrike.HungerStrike;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;

/**
 * Replaces the {@code ExtendedPlayer} capability the Forge builds used. A data attachment gives us
 * persistence, copy-on-death and client sync for free, which is why the sync/request packets and
 * the {@code PlayerHandler} death-data cache from the old versions are gone.
 *
 * <p>Fabric's attachment API is the direct counterpart of NeoForge's: the builder is registered
 * eagerly at class-init rather than through a {@code DeferredRegister}, and sync targeting is
 * explicit ({@link AttachmentSyncPredicate#targetOnly()}) where NeoForge implies it.
 */
public final class HungerStrikeAttachments {
    /**
     * Whether this player is on hunger strike. Only meaningful in {@link Config.Mode#LIST}.
     * Synced so the client can hide its own hunger bar without asking the server.
     */
    public static final AttachmentType<Boolean> ON_STRIKE = AttachmentRegistry.create(
            HungerStrike.id("on_strike"),
            builder -> builder
                    .initializer(() -> Boolean.FALSE)
                    .persistent(Codec.BOOL)
                    .copyOnDeath()
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.targetOnly()));

    private HungerStrikeAttachments() {}

    /** Forces class init, so the attachment type exists before the first player is loaded. */
    public static void register() {}
}
