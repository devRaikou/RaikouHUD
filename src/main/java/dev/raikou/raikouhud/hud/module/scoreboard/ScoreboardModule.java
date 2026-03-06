package dev.raikou.raikouhud.hud.module.scoreboard;

import dev.raikou.raikouhud.config.ConfigService;
import dev.raikou.raikouhud.config.model.module.ModuleConfig;
import dev.raikou.raikouhud.config.model.module.ScoreboardConfig;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ScoreboardModule implements HudModule {

    private final ConfigService configService;
    private final PlayerSessionService sessionService;
    private final TemplateCompiler templateCompiler;
    private final TemplateRenderer templateRenderer;
    private final ScoreboardRenderer scoreboardRenderer;

    private CompiledTemplate compiledTitle;
    private List<CompiledTemplate> compiledLines;

    public ScoreboardModule(
        ConfigService configService,
        PlayerSessionService sessionService,
        TemplateCompiler templateCompiler,
        TemplateRenderer templateRenderer,
        ScoreboardRenderer scoreboardRenderer
    ) {
        this.configService = Objects.requireNonNull(configService, "configService");
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService");
        this.templateCompiler = Objects.requireNonNull(templateCompiler, "templateCompiler");
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer");
        this.scoreboardRenderer = Objects.requireNonNull(scoreboardRenderer, "scoreboardRenderer");
        this.compiledTitle = templateCompiler.compile("");
        this.compiledLines = List.of();
    }

    @Override
    public HudModuleType type() {
        return HudModuleType.SCOREBOARD;
    }

    @Override
    public boolean isGloballyEnabled() {
        return configService.mainConfig().moduleToggles().scoreboard() && configService.scoreboardConfig().moduleConfig().enabled();
    }

    @Override
    public int updateIntervalTicks() {
        return configService.scoreboardConfig().moduleConfig().updateIntervalTicks();
    }

    @Override
    public void enable() {
        reload();
        tick(UpdateReason.MANUAL);
    }

    @Override
    public void disable() {
        for (PlayerSession session : sessionService.allSessions()) {
            Player player = Bukkit.getPlayer(session.playerId());
            if (player != null) {
                scoreboardRenderer.clear(player, session);
            }
        }
    }

    @Override
    public void reload() {
        ScoreboardConfig config = configService.scoreboardConfig();
        compiledTitle = templateCompiler.compile(config.title());
        compiledLines = config.lines().stream().map(templateCompiler::compile).toList();
        if (!isGloballyEnabled()) {
            disable();
        }
    }

    @Override
    public void onPlayerJoin(PlayerSession session) {
        Player player = Bukkit.getPlayer(session.playerId());
        if (player == null) {
            return;
        }
        if (!isEligible(player, session)) {
            return;
        }
        renderForPlayer(player, session);
    }

    @Override
    public void onPlayerQuit(UUID playerId) {
        sessionService.session(playerId).ifPresent(session -> {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                scoreboardRenderer.clear(player, session);
            }
        });
    }

    @Override
    public void tick(UpdateReason reason) {
        if (!isGloballyEnabled()) {
            return;
        }
        for (PlayerSession session : sessionService.allSessions()) {
            Player player = Bukkit.getPlayer(session.playerId());
            if (player == null || !isEligible(player, session)) {
                if (player != null) {
                    scoreboardRenderer.clear(player, session);
                }
                continue;
            }
            renderForPlayer(player, session);
        }
    }

    private void renderForPlayer(Player player, PlayerSession session) {
        RenderContext context = new RenderContext(player, session, Instant.now());
        String title = templateRenderer.render(compiledTitle, context);
        List<String> lines = compiledLines.stream().map(template -> templateRenderer.render(template, context)).toList();
        scoreboardRenderer.render(
            player,
            session,
            title,
            lines,
            configService.scoreboardConfig().takeoverMode()
        );
    }

    private boolean isEligible(Player player, PlayerSession session) {
        if (!session.isModuleEnabled(HudModuleType.SCOREBOARD)) {
            return false;
        }
        ModuleConfig moduleConfig = configService.scoreboardConfig().moduleConfig();
        if (!moduleConfig.permission().isBlank() && !player.hasPermission(moduleConfig.permission())) {
            return false;
        }
        return !moduleConfig.disabledWorlds().contains(player.getWorld().getName().toLowerCase(java.util.Locale.ROOT));
    }
}

