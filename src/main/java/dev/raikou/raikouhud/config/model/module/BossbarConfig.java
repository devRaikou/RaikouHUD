package dev.raikou.raikouhud.config.model.module;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public record BossbarConfig(
    ModuleConfig moduleConfig,
    BarColor color,
    BarStyle style,
    String title,
    BossbarProgressMode progressMode,
    double staticProgress,
    String progressPlaceholder
) {
}

