package dev.raikou.raikouhud.config.file;

import org.bukkit.plugin.java.JavaPlugin;

public final class TabConfigFile extends YamlBackedFile {

    public TabConfigFile(JavaPlugin plugin) {
        super(plugin, "tab.yml");
    }
}

