package dev.raikou.raikouhud.hud.placeholder;

import dev.raikou.raikouhud.hud.render.RenderContext;
import java.util.Optional;

public interface PlaceholderResolver {

    Optional<String> resolve(String token, RenderContext context);
}

