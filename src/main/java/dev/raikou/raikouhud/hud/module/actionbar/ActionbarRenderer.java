package dev.raikou.raikouhud.hud.module.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import dev.raikou.raikouhud.util.MiniMessageSupport;
import org.bukkit.entity.Player;

public final class ActionbarRenderer {

    public void render(Player player, String message) {
        String legacy = MiniMessageSupport.toLegacySection(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(legacy));
    }
}
