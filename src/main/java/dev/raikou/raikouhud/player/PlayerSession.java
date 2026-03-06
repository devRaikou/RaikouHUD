package dev.raikou.raikouhud.player;

import dev.raikou.raikouhud.hud.HudModuleType;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSession {

    private final UUID playerId;
    private final Set<HudModuleType> disabledModules;
    private final Map<String, Object> metadata;
    private boolean dirty;

    public PlayerSession(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        this.disabledModules = EnumSet.noneOf(HudModuleType.class);
        this.metadata = new ConcurrentHashMap<>();
        this.dirty = true;
    }

    public UUID playerId() {
        return playerId;
    }

    public boolean isModuleEnabled(HudModuleType type) {
        return !disabledModules.contains(type);
    }

    public boolean toggleModule(HudModuleType type) {
        if (disabledModules.contains(type)) {
            disabledModules.remove(type);
            return true;
        }
        disabledModules.add(type);
        return false;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    public void putMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object metadata(String key) {
        return metadata.get(key);
    }

    public Object removeMetadata(String key) {
        return metadata.remove(key);
    }
}
