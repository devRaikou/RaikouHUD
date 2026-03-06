package dev.raikou.raikouhud.hud;

import java.util.Locale;
import java.util.Optional;

public enum HudModuleType {
    SCOREBOARD,
    BOSSBAR,
    ACTIONBAR,
    TAB;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<HudModuleType> fromInput(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        String normalized = input.trim().toUpperCase(Locale.ROOT);
        for (HudModuleType type : values()) {
            if (type.name().equals(normalized)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}

