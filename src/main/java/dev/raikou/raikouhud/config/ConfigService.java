package dev.raikou.raikouhud.config;

import dev.raikou.raikouhud.config.file.ActionbarConfigFile;
import dev.raikou.raikouhud.config.file.BossbarConfigFile;
import dev.raikou.raikouhud.config.file.MainConfigFile;
import dev.raikou.raikouhud.config.file.ScoreboardConfigFile;
import dev.raikou.raikouhud.config.file.TabConfigFile;
import dev.raikou.raikouhud.config.model.LocaleConfig;
import dev.raikou.raikouhud.config.model.MainConfig;
import dev.raikou.raikouhud.config.model.ModuleToggles;
import dev.raikou.raikouhud.config.model.PerformanceConfig;
import dev.raikou.raikouhud.config.model.module.ActionbarConfig;
import dev.raikou.raikouhud.config.model.module.BossbarConfig;
import dev.raikou.raikouhud.config.model.module.BossbarProgressMode;
import dev.raikou.raikouhud.config.model.module.ModuleConfig;
import dev.raikou.raikouhud.config.model.module.ScoreboardConfig;
import dev.raikou.raikouhud.config.model.module.ScoreboardTakeoverMode;
import dev.raikou.raikouhud.config.model.module.TabConfig;
import dev.raikou.raikouhud.config.validate.ConfigValidator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigService {

    private final Logger logger;
    private final MainConfigFile mainConfigFile;
    private final ScoreboardConfigFile scoreboardConfigFile;
    private final BossbarConfigFile bossbarConfigFile;
    private final ActionbarConfigFile actionbarConfigFile;
    private final TabConfigFile tabConfigFile;

    private volatile MainConfig mainConfig;
    private volatile ScoreboardConfig scoreboardConfig;
    private volatile BossbarConfig bossbarConfig;
    private volatile ActionbarConfig actionbarConfig;
    private volatile TabConfig tabConfig;

    public ConfigService(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
        this.mainConfigFile = new MainConfigFile(plugin);
        this.scoreboardConfigFile = new ScoreboardConfigFile(plugin);
        this.bossbarConfigFile = new BossbarConfigFile(plugin);
        this.actionbarConfigFile = new ActionbarConfigFile(plugin);
        this.tabConfigFile = new TabConfigFile(plugin);
    }

    public void loadAll() {
        mainConfigFile.load();
        scoreboardConfigFile.load();
        bossbarConfigFile.load();
        actionbarConfigFile.load();
        tabConfigFile.load();
        mapAll();
    }

    public void reloadAll() {
        mainConfigFile.reload();
        scoreboardConfigFile.reload();
        bossbarConfigFile.reload();
        actionbarConfigFile.reload();
        tabConfigFile.reload();
        mapAll();
    }

    private void mapAll() {
        MainConfig mappedMain = mapMainConfig(mainConfigFile.configuration());
        int minInterval = mappedMain.performanceConfig().minUpdateIntervalTicks();
        mainConfig = mappedMain;
        scoreboardConfig = mapScoreboardConfig(scoreboardConfigFile.configuration(), minInterval);
        bossbarConfig = mapBossbarConfig(bossbarConfigFile.configuration(), minInterval);
        actionbarConfig = mapActionbarConfig(actionbarConfigFile.configuration(), minInterval);
        tabConfig = mapTabConfig(tabConfigFile.configuration(), minInterval);
    }

    private MainConfig mapMainConfig(YamlConfiguration config) {
        boolean debug = config.getBoolean("plugin.debug", false);
        LocaleConfig localeConfig = new LocaleConfig(
            normalizeLocale(config.getString("locale.default", "en_US")),
            normalizeLocale(config.getString("locale.fallback", "en_US"))
        );
        ModuleToggles moduleToggles = new ModuleToggles(
            config.getBoolean("modules.scoreboard", true),
            config.getBoolean("modules.bossbar", true),
            config.getBoolean("modules.actionbar", true),
            config.getBoolean("modules.tab", true)
        );
        int minInterval = Math.max(1, config.getInt("performance.min-update-interval-ticks", 10));
        int joinDelay = Math.max(0, config.getInt("performance.join-init-delay-ticks", 1));
        PerformanceConfig performanceConfig = new PerformanceConfig(minInterval, joinDelay);
        boolean placeholderApiEnabled = config.getBoolean("integrations.placeholderapi", true);
        return new MainConfig(debug, localeConfig, moduleToggles, performanceConfig, placeholderApiEnabled);
    }

    private ScoreboardConfig mapScoreboardConfig(YamlConfiguration config, int minInterval) {
        ModuleConfig moduleConfig = readModuleConfig(config, "scoreboard", minInterval);
        moduleConfig = ConfigValidator.sanitizeModuleConfig(moduleConfig, minInterval, logger, "scoreboard.yml");
        ScoreboardTakeoverMode takeoverMode = ConfigValidator.parseEnumOrDefault(
            config.getString("scoreboard.takeover-mode", "SOFT"),
            ScoreboardTakeoverMode.class,
            ScoreboardTakeoverMode.SOFT,
            logger,
            "scoreboard.takeover-mode"
        );
        String title = config.getString("scoreboard.title", "<aqua><bold>RaikouHUD</bold></aqua>");
        List<String> lines = config.getStringList("scoreboard.lines");
        if (lines.isEmpty()) {
            lines = List.of(
                "<gray>Player: <white>%player_name%</white></gray>",
                "<gray>Ping: <green>%player_ping%ms</green></gray>",
                "<gray>World: <aqua>%player_world%</aqua></gray>",
                "<gray>Online: <yellow>%server_online%</yellow></gray>"
            );
        }
        return new ScoreboardConfig(moduleConfig, takeoverMode, title, List.copyOf(lines));
    }

    private BossbarConfig mapBossbarConfig(YamlConfiguration config, int minInterval) {
        ModuleConfig moduleConfig = readModuleConfig(config, "bossbar", minInterval);
        moduleConfig = ConfigValidator.sanitizeModuleConfig(moduleConfig, minInterval, logger, "bossbar.yml");
        BarColor color = ConfigValidator.parseEnumOrDefault(
            config.getString("bossbar.color", "BLUE"),
            BarColor.class,
            BarColor.BLUE,
            logger,
            "bossbar.color"
        );
        BarStyle style = ConfigValidator.parseEnumOrDefault(
            config.getString("bossbar.style", "SEGMENTED_10"),
            BarStyle.class,
            BarStyle.SEGMENTED_10,
            logger,
            "bossbar.style"
        );
        BossbarProgressMode progressMode = ConfigValidator.parseEnumOrDefault(
            config.getString("bossbar.progress.mode", "STATIC"),
            BossbarProgressMode.class,
            BossbarProgressMode.STATIC,
            logger,
            "bossbar.progress.mode"
        );
        double staticProgress = ConfigValidator.clampProgress(
            config.getDouble("bossbar.progress.static-value", 1.0D),
            logger,
            "bossbar.progress.static-value"
        );
        String title = config.getString("bossbar.title", "<aqua>%player_name%</aqua> <gray>|</gray> <white>%server_online% online</white>");
        String progressPlaceholder = config.getString("bossbar.progress.placeholder", "%player_health_ratio%");
        return new BossbarConfig(moduleConfig, color, style, title, progressMode, staticProgress, progressPlaceholder);
    }

    private ActionbarConfig mapActionbarConfig(YamlConfiguration config, int minInterval) {
        ModuleConfig moduleConfig = readModuleConfig(config, "actionbar", minInterval);
        moduleConfig = ConfigValidator.sanitizeModuleConfig(moduleConfig, minInterval, logger, "actionbar.yml");
        String message = config.getString(
            "actionbar.message",
            "<yellow>Ping</yellow> <white>%player_ping%ms</white> <gray>|</gray> " +
                "<yellow>X:</yellow><white>%player_x%</white> " +
                "<yellow>Y:</yellow><white>%player_y%</white> " +
                "<yellow>Z:</yellow><white>%player_z%</white>"
        );
        return new ActionbarConfig(moduleConfig, message);
    }

    private TabConfig mapTabConfig(YamlConfiguration config, int minInterval) {
        ModuleConfig moduleConfig = readModuleConfig(config, "tab", minInterval);
        moduleConfig = ConfigValidator.sanitizeModuleConfig(moduleConfig, minInterval, logger, "tab.yml");
        List<String> header = config.getStringList("tab.header");
        if (header.isEmpty()) {
            header = List.of("<gold><bold>RaikouHUD</bold></gold>", "<gray>Online: <white>%server_online%</white></gray>");
        }
        List<String> footer = config.getStringList("tab.footer");
        if (footer.isEmpty()) {
            footer = List.of("<gray>TPS: <white>%server_tps_1m%</white></gray>", "<gray>Enjoy your game!</gray>");
        }
        return new TabConfig(moduleConfig, List.copyOf(header), List.copyOf(footer));
    }

    private ModuleConfig readModuleConfig(YamlConfiguration config, String root, int minInterval) {
        String prefix = root + ".";
        boolean enabled = config.getBoolean(prefix + "enabled", true);
        int interval = config.getInt(prefix + "update-interval-ticks", minInterval);
        String permission = config.getString(prefix + "permission", "");
        Set<String> disabledWorlds = config.getStringList(prefix + "disabled-worlds")
            .stream()
            .map(value -> value.toLowerCase(Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
        return new ModuleConfig(enabled, interval, permission == null ? "" : permission, disabledWorlds);
    }

    private String normalizeLocale(String raw) {
        if (raw == null || raw.isBlank()) {
            return "en_US";
        }
        String normalized = raw.trim();
        if (normalized.contains("-")) {
            normalized = normalized.replace('-', '_');
        }
        String[] parts = normalized.split("_");
        if (parts.length != 2) {
            logger.warning("Invalid locale '" + raw + "', using en_US.");
            return "en_US";
        }
        return parts[0].toLowerCase(Locale.ROOT) + "_" + parts[1].toUpperCase(Locale.ROOT);
    }

    public MainConfig mainConfig() {
        return mainConfig;
    }

    public ScoreboardConfig scoreboardConfig() {
        return scoreboardConfig;
    }

    public BossbarConfig bossbarConfig() {
        return bossbarConfig;
    }

    public ActionbarConfig actionbarConfig() {
        return actionbarConfig;
    }

    public TabConfig tabConfig() {
        return tabConfig;
    }
}
