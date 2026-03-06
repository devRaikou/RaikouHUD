package dev.raikou.raikouhud.config.model;

public record PerformanceConfig(
    int minUpdateIntervalTicks,
    int joinInitDelayTicks
) {
}

