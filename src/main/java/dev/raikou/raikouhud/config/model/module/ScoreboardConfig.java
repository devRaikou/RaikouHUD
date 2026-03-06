package dev.raikou.raikouhud.config.model.module;

import java.util.List;

public record ScoreboardConfig(
    ModuleConfig moduleConfig,
    ScoreboardTakeoverMode takeoverMode,
    String title,
    List<String> lines
) {
}

