package dev.raikou.raikouhud.player;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlayerSessionService {

    private final Map<UUID, PlayerSession> sessions;

    public PlayerSessionService() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public void initializeOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            create(player.getUniqueId());
        }
    }

    public PlayerSession create(UUID playerId) {
        return sessions.computeIfAbsent(playerId, PlayerSession::new);
    }

    public Optional<PlayerSession> session(UUID playerId) {
        return Optional.ofNullable(sessions.get(playerId));
    }

    public Collection<PlayerSession> allSessions() {
        return sessions.values();
    }

    public void remove(UUID playerId) {
        sessions.remove(playerId);
    }

    public void clear() {
        sessions.clear();
    }
}

