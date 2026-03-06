package dev.raikou.raikouhud.bootstrap;

import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginShutdown {

    private final JavaPlugin plugin;

    public PluginShutdown(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    public void disable(RuntimeContainer container) {
        if (container.hudService() != null) {
            container.hudService().stop();
        } else if (container.updateCoordinator() != null) {
            container.updateCoordinator().stop();
        }
        if (container.playerSessionService() != null) {
            container.playerSessionService().clear();
        }
        plugin.getLogger().info("RaikouHUD disabled.");
    }
}

