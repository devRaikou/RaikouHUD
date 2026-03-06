package dev.raikou.raikouhud.config.file;

import org.bukkit.plugin.java.JavaPlugin;

public final class ScoreboardConfigFile extends YamlBackedFile {

    public ScoreboardConfigFile(JavaPlugin plugin) {
        super(plugin, "scoreboard.yml");
    }
}

