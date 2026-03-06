package dev.raikou.raikouhud.hud;

import dev.raikou.raikouhud.hud.update.UpdateCoordinator;
import dev.raikou.raikouhud.hud.update.UpdateReason;
import dev.raikou.raikouhud.player.PlayerSession;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class HudService {

    private final UpdateCoordinator updateCoordinator;
    private final Map<HudModuleType, HudModule> modules;

    public HudService(UpdateCoordinator updateCoordinator) {
        this.updateCoordinator = Objects.requireNonNull(updateCoordinator, "updateCoordinator");
        this.modules = new EnumMap<>(HudModuleType.class);
    }

    public void register(HudModule module) {
        modules.put(module.type(), module);
    }

    public void start() {
        modules.values().forEach(HudModule::enable);
        rebuildScheduler();
    }

    public void stop() {
        updateCoordinator.stop();
        modules.values().forEach(HudModule::disable);
    }

    public void reload() {
        updateCoordinator.stop();
        modules.values().forEach(HudModule::reload);
        rebuildScheduler();
        modules.values().forEach(module -> module.tick(UpdateReason.RELOAD));
    }

    public void onPlayerJoin(PlayerSession session) {
        modules.values().forEach(module -> module.onPlayerJoin(session));
    }

    public void onPlayerQuit(UUID playerId) {
        modules.values().forEach(module -> module.onPlayerQuit(playerId));
    }

    public Collection<HudModule> modules() {
        return modules.values();
    }

    public HudModule module(HudModuleType type) {
        return modules.get(type);
    }

    private void rebuildScheduler() {
        updateCoordinator.rebuild(modules.values());
    }
}

