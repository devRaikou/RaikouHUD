package dev.raikou.raikouhud.config.model.module;

import java.util.Set;

public record ModuleConfig(
    boolean enabled,
    int updateIntervalTicks,
    String permission,
    Set<String> disabledWorlds
) {
}

