package dev.raikou.raikouhud.config.validate;

import dev.raikou.raikouhud.config.model.module.ModuleConfig;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

public final class ConfigValidator {

    private ConfigValidator() {
    }

    public static ModuleConfig sanitizeModuleConfig(
        ModuleConfig moduleConfig,
        int minIntervalTicks,
        Logger logger,
        String configName
    ) {
        int interval = moduleConfig.updateIntervalTicks();
        if (interval < minIntervalTicks) {
            logger.warning(configName + ": update-interval-ticks was below minimum, adjusted to " + minIntervalTicks + ".");
            interval = minIntervalTicks;
        }
        String permission = moduleConfig.permission() == null ? "" : moduleConfig.permission().trim();
        Set<String> disabledWorlds = moduleConfig.disabledWorlds()
            .stream()
            .filter(world -> world != null && !world.isBlank())
            .map(world -> world.toLowerCase(Locale.ROOT))
            .collect(java.util.stream.Collectors.toUnmodifiableSet());
        return new ModuleConfig(moduleConfig.enabled(), interval, permission, disabledWorlds);
    }

    public static <E extends Enum<E>> E parseEnumOrDefault(
        String raw,
        Class<E> enumType,
        E fallback,
        Logger logger,
        String configPath
    ) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Enum.valueOf(enumType, raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            logger.warning(configPath + ": invalid value '" + raw + "', using " + fallback.name() + ".");
            return fallback;
        }
    }

    public static double clampProgress(double progress, Logger logger, String path) {
        if (progress < 0.0D || progress > 1.0D) {
            logger.warning(path + ": value must be between 0.0 and 1.0, clamped.");
            return Math.max(0.0D, Math.min(1.0D, progress));
        }
        return progress;
    }
}

