package dev.raikou.raikouhud.hud.placeholder;

import dev.raikou.raikouhud.hud.render.RenderContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class PlaceholderService {

    private final List<PlaceholderResolver> resolvers;

    public PlaceholderService() {
        this.resolvers = new ArrayList<>();
    }

    public void registerResolver(PlaceholderResolver resolver) {
        resolvers.add(Objects.requireNonNull(resolver, "resolver"));
    }

    public void clearResolvers() {
        resolvers.clear();
    }

    public Optional<String> resolve(String token, RenderContext context) {
        for (PlaceholderResolver resolver : resolvers) {
            Optional<String> value = resolver.resolve(token, context);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }
}
