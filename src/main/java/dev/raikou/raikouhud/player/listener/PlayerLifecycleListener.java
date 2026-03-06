package dev.raikou.raikouhud.player.listener;

import dev.raikou.raikouhud.player.PlayerSessionService;
import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class PlayerLifecycleListener implements Listener {

    private final PlayerSessionService sessionService;

    public PlayerLifecycleListener(PlayerSessionService sessionService) {
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService");
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        sessionService.session(event.getPlayer().getUniqueId()).ifPresent(session -> session.markDirty());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        sessionService.session(event.getPlayer().getUniqueId()).ifPresent(session -> session.markDirty());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) {
            return;
        }
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            sessionService.session(event.getPlayer().getUniqueId()).ifPresent(session -> session.markDirty());
        }
    }
}

