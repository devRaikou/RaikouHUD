package dev.raikou.raikouhud.config.model;

public record MainConfig(
    boolean debug,
    LocaleConfig localeConfig,
    ModuleToggles moduleToggles,
    PerformanceConfig performanceConfig,
    boolean placeholderApiEnabled
) {
}

