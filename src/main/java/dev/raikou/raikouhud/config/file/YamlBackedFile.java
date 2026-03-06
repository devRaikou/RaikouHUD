package dev.raikou.raikouhud.config.file;

import java.io.File;
import java.util.Objects;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class YamlBackedFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private YamlConfiguration configuration;

    protected YamlBackedFile(JavaPlugin plugin, String fileName) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.fileName = Objects.requireNonNull(fileName, "fileName");
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder.");
        }
        this.file = new File(plugin.getDataFolder(), fileName);
    }

    public void load() {
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration configuration() {
        if (configuration == null) {
            throw new IllegalStateException("Configuration not loaded yet: " + fileName);
        }
        return configuration;
    }

    public String fileName() {
        return fileName;
    }
}

