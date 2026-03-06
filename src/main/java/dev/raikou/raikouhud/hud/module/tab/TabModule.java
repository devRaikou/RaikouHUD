package dev.raikou.raikouhud.hud.module.tab;

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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class TabModule implements HudModule {

    private static final String META_HASH = "tab.hash";

    private final ConfigService configService;
    private final PlayerSessionService sessionService;
    private final TemplateCompiler templateCompiler;
    private final TemplateRenderer templateRenderer;
    private final TabRenderer tabRenderer;

    private List<CompiledTemplate> compiledHeader;
    private List<CompiledTemplate> compiledFooter;

    public TabModule(
        ConfigService configService,
        PlayerSessionService sessionService,
        TemplateCompiler templateCompiler,
        TemplateRenderer templateRenderer,
        TabRenderer tabRenderer
    ) {
        this.configService = Objects.requireNonNull(configService, "configService");
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService");
        this.templateCompiler = Objects.requireNonNull(templateCompiler, "templateCompiler");
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer");
        this.tabRenderer = Objects.requireNonNull(tabRenderer, "tabRenderer");
        this.compiledHeader = List.of();
        this.compiledFooter = List.of();
    }

    @Override
    public HudModuleType type() {
        return HudModuleType.TAB;
    }

    @Override
    public boolean isGloballyEnabled() {
        return configService.mainConfig().moduleToggles().tab() && configService.tabConfig().moduleConfig().enabled();
    }

    @Override
    public int updateIntervalTicks() {
        return configService.tabConfig().moduleConfig().updateIntervalTicks();
    }

    @Override
    public void enable() {
        reload();
        tick(UpdateReason.MANUAL);
    }

    @Override
    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            tabRenderer.clear(player);
        }
        for (PlayerSession session : sessionService.allSessions()) {
            session.removeMetadata(META_HASH);
        }
    }

    @Override
    public void reload() {
        compiledHeader = configService.tabConfig().header().stream().map(templateCompiler::compile).toList();
        compiledFooter = configService.tabConfig().footer().stream().map(templateCompiler::compile).toList();
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
        sessionService.session(playerId).ifPresent(session -> session.removeMetadata(META_HASH));
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            tabRenderer.clear(player);
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
                if (player != null) {
                    tabRenderer.clear(player);
                }
                session.removeMetadata(META_HASH);
                continue;
            }
            render(player, session);
        }
    }

    private void render(Player player, PlayerSession session) {
        RenderContext context = new RenderContext(player, session, Instant.now());
        List<String> headerLines = compiledHeader.stream().map(template -> templateRenderer.render(template, context)).toList();
        List<String> footerLines = compiledFooter.stream().map(template -> templateRenderer.render(template, context)).toList();
        String hash = String.join("\n", headerLines) + "|" + String.join("\n", footerLines);

        if (hash.equals(session.metadata(META_HASH))) {
            return;
        }
        tabRenderer.render(player, headerLines, footerLines);
        session.putMetadata(META_HASH, hash);
    }

    private boolean isEligible(Player player, PlayerSession session) {
        if (!session.isModuleEnabled(HudModuleType.TAB)) {
            return false;
        }
        ModuleConfig moduleConfig = configService.tabConfig().moduleConfig();
        if (!moduleConfig.permission().isBlank() && !player.hasPermission(moduleConfig.permission())) {
            return false;
        }
        return !moduleConfig.disabledWorlds().contains(player.getWorld().getName().toLowerCase(Locale.ROOT));
    }
}

