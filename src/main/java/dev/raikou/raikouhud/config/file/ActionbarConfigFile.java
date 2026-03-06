package dev.raikou.raikouhud.config.file;

import org.bukkit.plugin.java.JavaPlugin;

public final class ActionbarConfigFile extends YamlBackedFile {

    public ActionbarConfigFile(JavaPlugin plugin) {
        super(plugin, "actionbar.yml");
    }
}

