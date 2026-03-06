package dev.raikou.raikouhud.bootstrap;

import dev.raikou.raikouhud.command.RaikouHudCommand;
import dev.raikou.raikouhud.config.ConfigService;
import dev.raikou.raikouhud.hud.HudService;
import dev.raikou.raikouhud.hud.placeholder.PlaceholderService;
import dev.raikou.raikouhud.hud.update.UpdateCoordinator;
import dev.raikou.raikouhud.i18n.LocaleService;
import dev.raikou.raikouhud.i18n.MessageService;
import dev.raikou.raikouhud.player.PlayerSessionService;

public final class RuntimeContainer {

    private ConfigService configService;
    private LocaleService localeService;
    private MessageService messageService;
    private PlaceholderService placeholderService;
    private PlayerSessionService playerSessionService;
    private UpdateCoordinator updateCoordinator;
    private HudService hudService;
    private RaikouHudCommand command;

    public ConfigService configService() {
        return configService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public LocaleService localeService() {
        return localeService;
    }

    public void setLocaleService(LocaleService localeService) {
        this.localeService = localeService;
    }

    public MessageService messageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public PlaceholderService placeholderService() {
        return placeholderService;
    }

    public void setPlaceholderService(PlaceholderService placeholderService) {
        this.placeholderService = placeholderService;
    }

    public PlayerSessionService playerSessionService() {
        return playerSessionService;
    }

    public void setPlayerSessionService(PlayerSessionService playerSessionService) {
        this.playerSessionService = playerSessionService;
    }

    public UpdateCoordinator updateCoordinator() {
        return updateCoordinator;
    }

    public void setUpdateCoordinator(UpdateCoordinator updateCoordinator) {
        this.updateCoordinator = updateCoordinator;
    }

    public HudService hudService() {
        return hudService;
    }

    public void setHudService(HudService hudService) {
        this.hudService = hudService;
    }

    public RaikouHudCommand command() {
        return command;
    }

    public void setCommand(RaikouHudCommand command) {
        this.command = command;
    }
}

