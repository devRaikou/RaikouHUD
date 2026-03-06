package dev.raikou.raikouhud.player.listener;

import dev.raikou.raikouhud.config.ConfigService;
import dev.raikou.raikouhud.hud.HudService;
import dev.raikou.raikouhud.player.PlayerSession;
import dev.raikou.raikouhud.player.PlayerSessionService;
import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerJoinQuitListener implements Listener {

    private final JavaPlugin plugin;
    private final ConfigService configService;
    private final PlayerSessionService sessionService;
    private final HudService hudService;

    public PlayerJoinQuitListener(
        JavaPlugin plugin,
        ConfigService configService,
        PlayerSessionService sessionService,
        HudService hudService
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.configService = Objects.requireNonNull(configService, "configService");
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService");
        this.hudService = Objects.requireNonNull(hudService, "hudService");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerSession session = sessionService.create(event.getPlayer().getUniqueId());
        int delay = configService.mainConfig().performanceConfig().joinInitDelayTicks();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> hudService.onPlayerJoin(session), delay);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        hudService.onPlayerQuit(event.getPlayer().getUniqueId());
        sessionService.remove(event.getPlayer().getUniqueId());
    }
}

