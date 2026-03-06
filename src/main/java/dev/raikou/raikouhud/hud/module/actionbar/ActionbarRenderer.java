package dev.raikou.raikouhud.hud.module.actionbar;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public final class ActionbarRenderer {

    public void render(Player player, String message) {
        String colored = ChatColor.translateAlternateColorCodes('&', message == null ? "" : message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colored));
    }
}

