package com.space_beyond.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** One-time client chat message with the creator's support and social link. */
public final class SupportMessage {
    private static final String LINKTREE_URL = "https://linktr.ee/trffuchs";
    private static boolean shown;

    private SupportMessage() {}

    public static void init() {
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(SupportMessage::onPlayerJoin);
    }

    private static void onPlayerJoin(LocalPlayer player) {
        if (shown) return;
        shown = true;

        player.sendSystemMessage(Component.literal("Enjoying Space Modernized: Beyond?")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal("\nSupport its development and find my socials, including Ko-fi: ")
                        .withStyle(ChatFormatting.GRAY))
                .append(supportLink()));
    }

    private static MutableComponent supportLink() {
        MutableComponent link = Component.literal("TRFFuchs Linktree")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE);
        return link.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, LINKTREE_URL)));
    }
}
