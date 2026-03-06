package dev.raikou.raikouhud.config.file;

import org.bukkit.plugin.java.JavaPlugin;

public final class MainConfigFile extends YamlBackedFile {

    public MainConfigFile(JavaPlugin plugin) {
        super(plugin, "config.yml");
    }
}

