package dev.raikou.raikouhud.i18n;

import dev.raikou.raikouhud.config.ConfigService;
import dev.raikou.raikouhud.config.model.LocaleConfig;
import dev.raikou.raikouhud.i18n.bundle.MessageBundle;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class LocaleService {

    private final JavaPlugin plugin;
    private final ConfigService configService;
    private final Logger logger;
    private final Map<String, MessageBundle> bundles;

    private MessageBundle activeBundle;
    private MessageBundle fallbackBundle;

    public LocaleService(JavaPlugin plugin, ConfigService configService) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.configService = Objects.requireNonNull(configService, "configService");
        this.logger = plugin.getLogger();
        this.bundles = new HashMap<>();
    }

    public void load() {
        saveDefaultLangIfMissing("en_US");
        saveDefaultLangIfMissing("tr_TR");

        bundles.clear();
        loadBundle("en_US");
        loadBundle("tr_TR");

        LocaleConfig localeConfig = configService.mainConfig().localeConfig();
        String activeCode = normalizeLocale(localeConfig.defaultLocale());
        String fallbackCode = normalizeLocale(localeConfig.fallbackLocale());

        fallbackBundle = bundles.getOrDefault(fallbackCode, bundles.get("en_US"));
        activeBundle = bundles.getOrDefault(activeCode, fallbackBundle);

        if (!bundles.containsKey(activeCode)) {
            logger.warning("Locale '" + activeCode + "' not found. Using fallback locale.");
        }
        if (!bundles.containsKey(fallbackCode)) {
            logger.warning("Fallback locale '" + fallbackCode + "' not found. Using en_US.");
        }
    }

    public String message(String key) {
        String active = activeBundle == null ? null : activeBundle.message(key);
        if (active != null) {
            return active;
        }
        String fallback = fallbackBundle == null ? null : fallbackBundle.message(key);
        return fallback == null ? key : fallback;
    }

    public String activeLocaleCode() {
        return activeBundle == null ? "en_US" : activeBundle.localeCode();
    }

    private void saveDefaultLangIfMissing(String localeCode) {
        File file = new File(plugin.getDataFolder(), "lang/" + localeCode + ".yml");
        if (file.exists()) {
            return;
        }
        plugin.saveResource("lang/" + localeCode + ".yml", false);
    }

    private void loadBundle(String localeCode) {
        File file = new File(plugin.getDataFolder(), "lang/" + localeCode + ".yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        Map<String, String> flat = new HashMap<>();
        flatten(configuration, "", flat);
        bundles.put(localeCode, new MessageBundle(localeCode, Map.copyOf(flat)));
    }

    private void flatten(ConfigurationSection section, String prefix, Map<String, String> target) {
        for (String key : section.getKeys(false)) {
            String path = prefix.isEmpty() ? key : prefix + "." + key;
            Object value = section.get(key);
            if (value instanceof ConfigurationSection nested) {
                flatten(nested, path, target);
                continue;
            }
            if (value != null) {
                target.put(path, String.valueOf(value));
            }
        }
    }

    private String normalizeLocale(String raw) {
        if (raw == null || raw.isBlank()) {
            return "en_US";
        }
        String normalized = raw.trim().replace('-', '_');
        String[] parts = normalized.split("_");
        if (parts.length != 2) {
            return "en_US";
        }
        return parts[0].toLowerCase(Locale.ROOT) + "_" + parts[1].toUpperCase(Locale.ROOT);
    }
}

