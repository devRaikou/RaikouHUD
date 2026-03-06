package dev.raikou.raikouhud.config;

public final class ConfigReloader {

    private final ConfigService configService;

    public ConfigReloader(ConfigService configService) {
        this.configService = configService;
    }

    public void reload() {
        configService.reloadAll();
    }
}

