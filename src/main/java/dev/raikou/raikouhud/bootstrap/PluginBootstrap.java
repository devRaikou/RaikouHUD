package dev.raikou.raikouhud.bootstrap;

import dev.raikou.raikouhud.command.RaikouHudCommand;
import dev.raikou.raikouhud.command.subcommand.HelpSubcommand;
import dev.raikou.raikouhud.command.subcommand.ReloadSubcommand;
import dev.raikou.raikouhud.command.subcommand.StatusSubcommand;
import dev.raikou.raikouhud.command.subcommand.ToggleSubcommand;
import dev.raikou.raikouhud.command.subcommand.VersionSubcommand;
import dev.raikou.raikouhud.config.ConfigService;
import dev.raikou.raikouhud.hud.HudService;
import dev.raikou.raikouhud.hud.module.actionbar.ActionbarModule;
import dev.raikou.raikouhud.hud.module.actionbar.ActionbarRenderer;
import dev.raikou.raikouhud.hud.module.bossbar.BossbarModule;
import dev.raikou.raikouhud.hud.module.bossbar.BossbarRenderer;
import dev.raikou.raikouhud.hud.module.scoreboard.ScoreboardModule;
import dev.raikou.raikouhud.hud.module.scoreboard.ScoreboardRenderer;
import dev.raikou.raikouhud.hud.module.tab.TabModule;
import dev.raikou.raikouhud.hud.module.tab.TabRenderer;
import dev.raikou.raikouhud.hud.placeholder.BuiltinPlaceholderResolver;
import dev.raikou.raikouhud.hud.placeholder.PlaceholderApiResolver;
import dev.raikou.raikouhud.hud.placeholder.PlaceholderService;
import dev.raikou.raikouhud.hud.render.TemplateCompiler;
import dev.raikou.raikouhud.hud.render.TemplateRenderer;
import dev.raikou.raikouhud.hud.update.UpdateCoordinator;
import dev.raikou.raikouhud.i18n.LocaleService;
import dev.raikou.raikouhud.i18n.MessageService;
import dev.raikou.raikouhud.player.PlayerSessionService;
import dev.raikou.raikouhud.player.listener.PlayerJoinQuitListener;
import dev.raikou.raikouhud.player.listener.PlayerLifecycleListener;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginBootstrap {

    private final JavaPlugin plugin;

    public PluginBootstrap(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    public void enable(RuntimeContainer container) {
        ConfigService configService = new ConfigService(plugin);
        configService.loadAll();
        container.setConfigService(configService);

        LocaleService localeService = new LocaleService(plugin, configService);
        localeService.load();
        container.setLocaleService(localeService);

        MessageService messageService = new MessageService(localeService);
        container.setMessageService(messageService);

        PlaceholderService placeholderService = new PlaceholderService();
        rebuildPlaceholderChain(placeholderService, configService);
        container.setPlaceholderService(placeholderService);

        PlayerSessionService playerSessionService = new PlayerSessionService();
        playerSessionService.initializeOnlinePlayers();
        container.setPlayerSessionService(playerSessionService);

        TemplateCompiler templateCompiler = new TemplateCompiler();
        TemplateRenderer templateRenderer = new TemplateRenderer(placeholderService);

        UpdateCoordinator updateCoordinator = new UpdateCoordinator(plugin);
        container.setUpdateCoordinator(updateCoordinator);

        HudService hudService = new HudService(updateCoordinator);
        hudService.register(new ScoreboardModule(
            configService,
            playerSessionService,
            templateCompiler,
            templateRenderer,
            new ScoreboardRenderer()
        ));
        hudService.register(new BossbarModule(
            configService,
            playerSessionService,
            templateCompiler,
            templateRenderer,
            new BossbarRenderer()
        ));
        hudService.register(new ActionbarModule(
            configService,
            playerSessionService,
            templateCompiler,
            templateRenderer,
            new ActionbarRenderer()
        ));
        hudService.register(new TabModule(
            configService,
            playerSessionService,
            templateCompiler,
            templateRenderer,
            new TabRenderer()
        ));
        hudService.start();
        container.setHudService(hudService);

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(
            new PlayerJoinQuitListener(plugin, configService, playerSessionService, hudService),
            plugin
        );
        pluginManager.registerEvents(new PlayerLifecycleListener(playerSessionService), plugin);

        RaikouHudCommand command = new RaikouHudCommand(
            plugin.getLogger(),
            messageService,
            List.of(
                new HelpSubcommand(messageService),
                new ReloadSubcommand(messageService, () -> {
                    configService.reloadAll();
                    localeService.load();
                    rebuildPlaceholderChain(placeholderService, configService);
                    hudService.reload();
                }),
                new StatusSubcommand(messageService, hudService, playerSessionService),
                new ToggleSubcommand(messageService, playerSessionService, hudService),
                new VersionSubcommand(plugin, messageService)
            )
        );
        container.setCommand(command);

        PluginCommand pluginCommand = plugin.getCommand("raikouhud");
        if (pluginCommand == null) {
            throw new IllegalStateException("Command 'raikouhud' was not found in plugin.yml.");
        }
        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);

        playerSessionService.allSessions().forEach(hudService::onPlayerJoin);
        plugin.getLogger().info("RaikouHUD enabled.");
    }

    private void rebuildPlaceholderChain(PlaceholderService placeholderService, ConfigService configService) {
        placeholderService.clearResolvers();
        placeholderService.registerResolver(new BuiltinPlaceholderResolver());
        placeholderService.registerResolver(new PlaceholderApiResolver(
            plugin.getLogger(),
            configService.mainConfig().placeholderApiEnabled()
        ));
    }
}

