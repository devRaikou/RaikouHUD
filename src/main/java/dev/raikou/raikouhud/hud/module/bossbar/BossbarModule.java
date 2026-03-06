package dev.raikou.raikouhud.hud.module.bossbar;

import dev.raikou.raikouhud.config.ConfigService;
import dev.raikou.raikouhud.config.model.module.BossbarConfig;
import dev.raikou.raikouhud.config.model.module.BossbarProgressMode;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class BossbarModule implements HudModule {

    private final ConfigService configService;
    private final PlayerSessionService sessionService;
    private final TemplateCompiler templateCompiler;
    private final TemplateRenderer templateRenderer;
    private final BossbarRenderer bossbarRenderer;
    private final Map<UUID, BossbarSession> bars;

    private CompiledTemplate compiledTitle;
    private CompiledTemplate compiledProgress;

    public BossbarModule(
        ConfigService configService,
        PlayerSessionService sessionService,
        TemplateCompiler templateCompiler,
        TemplateRenderer templateRenderer,
        BossbarRenderer bossbarRenderer
    ) {
        this.configService = Objects.requireNonNull(configService, "configService");
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService");
        this.templateCompiler = Objects.requireNonNull(templateCompiler, "templateCompiler");
        this.templateRenderer = Objects.requireNonNull(templateRenderer, "templateRenderer");
        this.bossbarRenderer = Objects.requireNonNull(bossbarRenderer, "bossbarRenderer");
        this.bars = new ConcurrentHashMap<>();
        this.compiledTitle = templateCompiler.compile("");
        this.compiledProgress = templateCompiler.compile("%player_health_ratio%");
    }

    @Override
    public HudModuleType type() {
        return HudModuleType.BOSSBAR;
    }

    @Override
    public boolean isGloballyEnabled() {
        return configService.mainConfig().moduleToggles().bossbar() && configService.bossbarConfig().moduleConfig().enabled();
    }

    @Override
    public int updateIntervalTicks() {
        return configService.bossbarConfig().moduleConfig().updateIntervalTicks();
    }

    @Override
    public void enable() {
        reload();
        tick(UpdateReason.MANUAL);
    }

    @Override
    public void disable() {
        for (Map.Entry<UUID, BossbarSession> entry : bars.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                entry.getValue().bossBar().removePlayer(player);
            }
            entry.getValue().bossBar().removeAll();
        }
        bars.clear();
    }

    @Override
    public void reload() {
        BossbarConfig config = configService.bossbarConfig();
        compiledTitle = templateCompiler.compile(config.title());
        compiledProgress = templateCompiler.compile(config.progressPlaceholder());
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
        updateBossbar(player, session);
    }

    @Override
    public void onPlayerQuit(UUID playerId) {
        BossbarSession removed = bars.remove(playerId);
        if (removed == null) {
            return;
        }
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            removed.bossBar().removePlayer(player);
        }
        removed.bossBar().removeAll();
    }

    @Override
    public void tick(UpdateReason reason) {
        if (!isGloballyEnabled()) {
            return;
        }
        for (PlayerSession session : sessionService.allSessions()) {
            Player player = Bukkit.getPlayer(session.playerId());
            if (player == null || !isEligible(player, session)) {
                onPlayerQuit(session.playerId());
                continue;
            }
            updateBossbar(player, session);
        }
    }

    private void updateBossbar(Player player, PlayerSession session) {
        BossbarConfig config = configService.bossbarConfig();
        RenderContext context = new RenderContext(player, session, Instant.now());
        String title = templateRenderer.render(compiledTitle, context);
        double progress = resolveProgress(config, context);
        String hash = title + "|" + progress;

        BossbarSession barSession = bars.computeIfAbsent(player.getUniqueId(), ignored -> {
            var created = new BossbarSession(bossbarRenderer.create(config));
            created.bossBar().addPlayer(player);
            return created;
        });

        if (!barSession.bossBar().getPlayers().contains(player)) {
            barSession.bossBar().addPlayer(player);
        }

        if (!hash.equals(barSession.lastHash())) {
            bossbarRenderer.apply(barSession.bossBar(), title, progress);
            barSession.setLastHash(hash);
        }
    }

    private double resolveProgress(BossbarConfig config, RenderContext context) {
        if (config.progressMode() == BossbarProgressMode.STATIC) {
            return clamp(config.staticProgress());
        }

        String raw = templateRenderer.render(compiledProgress, context).trim();
        try {
            double value = Double.parseDouble(raw);
            if (value > 1.0D && value <= 100.0D) {
                return clamp(value / 100.0D);
            }
            return clamp(value);
        } catch (NumberFormatException exception) {
            return config.staticProgress();
        }
    }

    private boolean isEligible(Player player, PlayerSession session) {
        if (!session.isModuleEnabled(HudModuleType.BOSSBAR)) {
            return false;
        }
        ModuleConfig moduleConfig = configService.bossbarConfig().moduleConfig();
        if (!moduleConfig.permission().isBlank() && !player.hasPermission(moduleConfig.permission())) {
            return false;
        }
        return !moduleConfig.disabledWorlds().contains(player.getWorld().getName().toLowerCase(Locale.ROOT));
    }

    private double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}
