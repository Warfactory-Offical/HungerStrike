package com.texelsaurus.minecraft.hungerstrike;

import com.texelsaurus.minecraft.hungerstrike.service.CommonConfig;
import com.texelsaurus.minecraft.hungerstrike.service.CommonNetworking;
import com.texelsaurus.minecraft.hungerstrike.service.CommonPlayerHandler;

import java.util.ServiceLoader;

public final class ModServices
{
    public static final CommonNetworking NETWORK = load(CommonNetworking.class);
    public static final CommonConfig CONFIG = load(CommonConfig.class);
    public static final CommonPlayerHandler PLAYER_HANDLER = load(CommonPlayerHandler.class);

    private static <T> T load(Class<T> clazz) {
        final T service = ServiceLoader.load(clazz).findFirst().orElseThrow();
        return service;
    }
}
