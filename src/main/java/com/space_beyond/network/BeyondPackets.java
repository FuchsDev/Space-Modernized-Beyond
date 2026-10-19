package com.space_beyond.network;

import java.util.ArrayList;
import java.util.List;

import com.space_beyond.BeyondMod;

import net.minecraft.resources.ResourceLocation;
//? if >= 1.21.1 {
/*import com.space_m.platform.SpacePlatform;
import dev.architectury.networking.NetworkManager;
*///?}

/**
 * Paket-IDs von Beyond. Senden/Empfangen laeuft ueber {@code com.space_m.network.SpacePacketHandler}
 * ({@code sendToPlayer}, {@code registerS2C}); auf dem dedizierten 1.21.1-Server meldet {@link #register} nur die Typen an.
 */
public final class BeyondPackets {
    private static final List<ResourceLocation> S2C_IDS = new ArrayList<>();
    private static boolean registered;

    /** Sternkarte: besuchte Himmelskoerper des Spielers (Infotafel). */
    public static final ResourceLocation VISITED_BODIES = s2c("visited_bodies");

    private BeyondPackets() {}

    public static ResourceLocation s2c(String path) {
        ResourceLocation id = BeyondMod.id(path);
        S2C_IDS.add(id);
        return id;
    }

    public static void register() {
        if (registered) return;
        registered = true;
        //? if >= 1.21.1 {
        /*if (!SpacePlatform.isClient()) {
            for (ResourceLocation id : S2C_IDS) NetworkManager.registerS2CPayloadType(id);
        }
        *///?}
    }
}
