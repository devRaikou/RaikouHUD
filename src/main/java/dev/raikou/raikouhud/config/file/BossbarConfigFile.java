package dev.raikou.raikouhud.config.file;

import org.bukkit.plugin.java.JavaPlugin;

public final class BossbarConfigFile extends YamlBackedFile {

    public BossbarConfigFile(JavaPlugin plugin) {
        super(plugin, "bossbar.yml");
    }
}

