package dev.raikou.raikouhud.hud.module.tab;

import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public final class TabRenderer {

    public void render(Player player, List<String> headerLines, List<String> footerLines) {
        String header = joinAndColorize(headerLines);
        String footer = joinAndColorize(footerLines);
        player.setPlayerListHeaderFooter(header, footer);
    }

    public void clear(Player player) {
        player.setPlayerListHeaderFooter("", "");
    }

    private String joinAndColorize(List<String> lines) {
        return ChatColor.translateAlternateColorCodes('&', String.join("\n", lines));
    }
}

