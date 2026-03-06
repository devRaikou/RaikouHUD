package dev.raikou.raikouhud.config.model.module;

import java.util.List;

public record TabConfig(
    ModuleConfig moduleConfig,
    List<String> header,
    List<String> footer
) {
}

