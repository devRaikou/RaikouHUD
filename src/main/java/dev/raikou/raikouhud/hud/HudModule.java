package dev.raikou.raikouhud.hud;

import dev.raikou.raikouhud.hud.update.UpdateReason;
import dev.raikou.raikouhud.player.PlayerSession;
import java.util.UUID;

public interface HudModule {

    HudModuleType type();

    boolean isGloballyEnabled();

    int updateIntervalTicks();

    void enable();

    void disable();

    void reload();

    void onPlayerJoin(PlayerSession session);

    void onPlayerQuit(UUID playerId);

    void tick(UpdateReason reason);
}

