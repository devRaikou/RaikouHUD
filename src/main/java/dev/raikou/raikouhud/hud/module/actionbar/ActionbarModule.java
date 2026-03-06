package dev.raikou.raikouhud.hud.module.actionbar;

import dev.raikou.raikouhud.config.ConfigService;
import dev.raikou.raikouhud.config.model.module.ModuleConfig;
import dev.raikou.raikouhud.hud.HudModule;
import dev.raikou.raikouhud.hud.HudModuleType;
import dev.raikou.raikouhud.hud.render.CompiledTemplate;
import dev.raikou.raikouhud.hud.render.RenderContext;
import dev.raikou.raikouhud.hud.render.TemplateCompiler;
import dev.raikou.raikouhud.hud.render.TemplateRenderer;
import dev.raikou.raikouhud.hud.update.UpdateReason;
import dev.raikou.raikouhud.player.PlayerSession;
import dev.raikou.raikouhud.player.PlayerSessionService;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ActionbarModule implements HudModule {

    private final ConfigService configService;
    private final PlayerSessionService sessionService;
    private final TemplateCompiler templateCompiler;
    private final TemplateRenderer templateRenderer;
    private final ActionbarRenderer actionbarRenderer;

    private CompiledTemplate compiledMessage;

    public ActionbarModule(
        ConfigService configService,
        PlayerSessionService sessionService,
        TemplateCompiler templateCompiler,
        TemplateRenderer templateRenderer,
        ActionbarRenderer actionbarRenderer
    ) {
        this.configService = Objects.requireNonNull(configService, "configService");
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService");
        this.templateCompiler = Objects.requireNonNull(templateCompiler, "templateCompiler");
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer");
        this.actionbarRenderer = Objects.requireNonNull(actionbarRenderer, "actionbarRenderer");
        this.compiledMessage = templateCompiler.compile("");
    }

    @Override
    public HudModuleType type() {
        return HudModuleType.ACTIONBAR;
    }

    @Override
    public boolean isGloballyEnabled() {
        return configService.mainConfig().moduleToggles().actionbar() && configService.actionbarConfig().moduleConfig().enabled();
    }

    @Override
    public int updateIntervalTicks() {
        return configService.actionbarConfig().moduleConfig().updateIntervalTicks();
    }

    @Override
    public void enable() {
        reload();
    }

    @Override
    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            actionbarRenderer.render(player, "");
        }
    }

    @Override
    public void reload() {
        compiledMessage = templateCompiler.compile(configService.actionbarConfig().message());
        if (!isGloballyEnabled()) {
            disable();
        }
    }

    @Override
    public void onPlayerJoin(PlayerSession session) {
        Player player = Bukkit.getPlayer(session.playerId());
        if (player == null || !isEligible(player, session)) {
            return;
        }
        render(player, session);
    }

    @Override
    public void onPlayerQuit(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            actionbarRenderer.render(player, "");
        }
    }

    @Override
    public void tick(UpdateReason reason) {
        if (!isGloballyEnabled()) {
            return;
        }
        for (PlayerSession session : sessionService.allSessions()) {
            Player player = Bukkit.getPlayer(session.playerId());
            if (player == null || !isEligible(player, session)) {
                continue;
            }
            render(player, session);
        }
    }

    private void render(Player player, PlayerSession session) {
        RenderContext context = new RenderContext(player, session, Instant.now());
        String message = templateRenderer.render(compiledMessage, context);
        actionbarRenderer.render(player, message);
    }

    private boolean isEligible(Player player, PlayerSession session) {
        if (!session.isModuleEnabled(HudModuleType.ACTIONBAR)) {
            return false;
        }
        ModuleConfig moduleConfig = configService.actionbarConfig().moduleConfig();
        if (!moduleConfig.permission().isBlank() && !player.hasPermission(moduleConfig.permission())) {
            return false;
        }
        return !moduleConfig.disabledWorlds().contains(player.getWorld().getName().toLowerCase(Locale.ROOT));
    }
}

