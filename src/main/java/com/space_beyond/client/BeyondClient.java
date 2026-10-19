package com.space_beyond.client;

import com.space_beyond.BeyondMod;
import com.space_beyond.entity.mob.client.RenderInfected;
import com.space_beyond.entity.mob.infected.InfectedMobs;
import com.space_beyond.item.survival.SurvivalItems;
import com.space_beyond.machine.misc.client.GUIBeyondStarMap;
import com.space_beyond.mixin.client.MenuScreensAccessor;
import com.space_beyond.network.BeyondPackets;
import com.space_m.machine.misc.MiscMenus;
import com.space_m.network.SpacePacketHandler;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/**
 * Client-Einstieg von Beyond (nur physischer Client, aus {@code BeyondRegistry.init()} im Mod-Konstruktor).
 * Enthaelt die Client-Anmeldungen, die im Privat-Repo in {@code SpaceClient} und {@code MobClient} standen.
 */
public final class BeyondClient {
    private static boolean initialized;
    private static boolean starMapInstalled;

    private BeyondClient() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init() {
        if (initialized) return;
        initialized = true;

        SupportMessage.init();

        // Maschinen-BERs auf Raptors Renderpfad (Anmeldung per enqueueWork im Client-Setup, nach den alten BERs)
        com.space_beyond.client.render.RaptorMachineRenders.init();

        // Sternkarte: besuchte Koerper
        SpacePacketHandler.registerS2C(BeyondPackets.VISITED_BODIES, com.space_beyond.celestial.VisitedBodies.Packet::decode,
                (pkt, ctx) -> ctx.queue(pkt::apply));

        // Wurfgeschosse des Ueberlebens-Sets (Molotow, Nagelbombe): Vanilla-Item-Renderer
        EntityRendererRegistry.register(SurvivalItems.MOLOTOV_ENTITY, (EntityRendererProvider) ThrownItemRenderer::new);
        EntityRendererRegistry.register(SurvivalItems.NAIL_BOMB_ENTITY, (EntityRendererProvider) ThrownItemRenderer::new);

        // Infizierte von Sporos: Zombiemodell mit eigenen Texturen
        EntityRendererRegistry.register(InfectedMobs.SPORELING,
                (EntityRendererProvider) ctx -> new RenderInfected(ctx, BeyondMod.id("textures/entity/infected/sporeling.png"), 0.95F));
        EntityRendererRegistry.register(InfectedMobs.LURKER,
                (EntityRendererProvider) ctx -> new RenderInfected(ctx, BeyondMod.id("textures/entity/infected/lurker.png"), 1.0F));
        EntityRendererRegistry.register(InfectedMobs.BLINDCAP,
                (EntityRendererProvider) ctx -> new RenderInfected(ctx, BeyondMod.id("textures/entity/infected/blindcap.png"), 1.05F));
        EntityRendererRegistry.register(InfectedMobs.MYCOBRUTE,
                (EntityRendererProvider) ctx -> new RenderInfected(ctx, BeyondMod.id("textures/entity/infected/mycobrute.png"), 1.35F));

        // Bosse, Orbitalschlaege, Kristallsplitter, Partikel
        com.space_beyond.entity.mob.boss.client.BossClient.init();

        // StarDar: neue Sternkarte statt der Original-Oberflaeche. Erst tauschen, wenn Space Modernized seinen Bildschirm
        // angemeldet hat (Client-Setup) - eine zweite Anmeldung wuerde werfen, CLIENT_STARTED kommt auf Forge zu frueh.
        ClientTickEvent.CLIENT_POST.register(mc -> {
            if (starMapInstalled) return;
            var screens = MenuScreensAccessor.beyond$screens();
            var type = MiscMenus.STARDAR.get();
            if (!screens.containsKey(type)) return;
            screens.put(type, (net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor) (menu, inv, title) ->
                    new GUIBeyondStarMap((com.space_m.machine.misc.ContainerStardar) menu, inv, title));
            starMapInstalled = true;
        });
    }
}
