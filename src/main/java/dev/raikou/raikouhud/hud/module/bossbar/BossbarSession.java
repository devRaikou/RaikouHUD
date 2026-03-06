package dev.raikou.raikouhud.hud.module.bossbar;

import org.bukkit.boss.BossBar;

public final class BossbarSession {

    private final BossBar bossBar;
    private String lastHash;

    public BossbarSession(BossBar bossBar) {
        this.bossBar = bossBar;
        this.lastHash = "";
    }

    public BossBar bossBar() {
        return bossBar;
    }

    public String lastHash() {
        return lastHash;
    }

    public void setLastHash(String lastHash) {
        this.lastHash = lastHash;
    }
}

