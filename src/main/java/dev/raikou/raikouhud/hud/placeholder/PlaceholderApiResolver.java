package dev.raikou.raikouhud.hud.placeholder;

import dev.raikou.raikouhud.hud.render.RenderContext;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class PlaceholderApiResolver implements PlaceholderResolver {

    private final Logger logger;
    private final boolean enabled;
    private final Method setPlaceholdersMethod;

    public PlaceholderApiResolver(Logger logger, boolean pluginConfigEnabled) {
        this.logger = logger;
        if (!pluginConfigEnabled) {
            this.enabled = false;
            this.setPlaceholdersMethod = null;
            return;
        }

        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin placeholderApiPlugin = pluginManager.getPlugin("PlaceholderAPI");
        if (placeholderApiPlugin == null || !placeholderApiPlugin.isEnabled()) {
            this.enabled = false;
            this.setPlaceholdersMethod = null;
            return;
        }

        Method method = null;
        try {
            Class<?> apiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            method = apiClass.getMethod("setPlaceholders", Player.class, String.class);
        } catch (ReflectiveOperationException exception) {
            logger.warning("PlaceholderAPI detected but its API could not be resolved. Integration disabled.");
        }

        this.enabled = method != null;
        this.setPlaceholdersMethod = method;
    }

    @Override
    public Optional<String> resolve(String token, RenderContext context) {
        if (!enabled || setPlaceholdersMethod == null) {
            return Optional.empty();
        }

        try {
            String wrapped = "%" + token + "%";
            String resolved = (String) setPlaceholdersMethod.invoke(null, context.player(), wrapped);
            if (resolved == null || resolved.equals(wrapped)) {
                return Optional.empty();
            }
            return Optional.of(resolved);
        } catch (ReflectiveOperationException exception) {
            logger.warning("PlaceholderAPI resolution failed for token: " + token);
            return Optional.empty();
        }
    }
}

