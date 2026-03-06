package dev.raikou.raikouhud.hud.update;

import dev.raikou.raikouhud.hud.HudModule;
import java.util.List;

public record UpdateBucket(
    int intervalTicks,
    List<HudModule> modules
) {
}

