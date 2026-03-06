package dev.raikou.raikouhud.hud.module.bossbar;

import dev.raikou.raikouhud.config.model.module.BossbarConfig;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;

public final class BossbarRenderer {

    public BossBar create(BossbarConfig config) {
        BossBar bar = Bukkit.createBossBar(
            colorize(config.title()),
            config.color(),
            config.style()
        );
        bar.setVisible(true);
        return bar;
    }

    public void apply(BossBar bossBar, String title, double progress) {
        bossBar.setTitle(colorize(title));
        bossBar.setProgress(Math.max(0.0D, Math.min(1.0D, progress)));
    }

    private String colorize(String value) {
        return ChatColor.translateAlternateColorCodes('&', value == null ? "" : value);
    }
}

