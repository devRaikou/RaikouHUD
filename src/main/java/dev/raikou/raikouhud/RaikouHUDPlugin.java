package dev.raikou.raikouhud;

import dev.raikou.raikouhud.bootstrap.PluginBootstrap;
import dev.raikou.raikouhud.bootstrap.PluginShutdown;
import dev.raikou.raikouhud.bootstrap.RuntimeContainer;
import org.bukkit.plugin.java.JavaPlugin;

public final class RaikouHUDPlugin extends JavaPlugin {

    private RuntimeContainer runtimeContainer;

    @Override
    public void onEnable() {
        this.runtimeContainer = new RuntimeContainer();
        new PluginBootstrap(this).enable(runtimeContainer);
    }

    @Override
    public void onDisable() {
        if (runtimeContainer == null) {
            return;
        }
        new PluginShutdown(this).disable(runtimeContainer);
        runtimeContainer = null;
    }

    public RuntimeContainer runtime() {
        return runtimeContainer;
    }
}

