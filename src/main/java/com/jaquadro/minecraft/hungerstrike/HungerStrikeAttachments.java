package com.jaquadro.minecraft.hungerstrike;

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
     * Whether this player is on hunger strike. Only meaningful in {@link Config.Mode#LIST}.
     * Synced so the client can hide its own hunger bar without asking the server.
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> ON_STRIKE =
            ATTACHMENT_TYPES.register("on_strike", () -> AttachmentType
                    .builder(() -> Boolean.FALSE)
                    .serialize(Codec.BOOL.fieldOf("enabled"))
                    .copyOnDeath()
                    .sync(ByteBufCodecs.BOOL)
                    .build());

    private HungerStrikeAttachments() {}
}
