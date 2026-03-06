package dev.raikou.raikouhud.hud.render;

import dev.raikou.raikouhud.player.PlayerSession;
import java.time.Instant;
import org.bukkit.entity.Player;

public record RenderContext(
    Player player,
    PlayerSession session,
    Instant now
) {
}

