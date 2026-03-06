package dev.raikou.raikouhud.hud.render;

import dev.raikou.raikouhud.hud.placeholder.PlaceholderService;
import java.util.Objects;

public final class TemplateRenderer {

    private final PlaceholderService placeholderService;

    public TemplateRenderer(PlaceholderService placeholderService) {
        this.placeholderService = Objects.requireNonNull(placeholderService, "placeholderService");
    }

    public String render(CompiledTemplate template, RenderContext context) {
        StringBuilder output = new StringBuilder();
        for (CompiledTemplate.Segment segment : template.segments()) {
            if (segment instanceof CompiledTemplate.LiteralSegment literal) {
                output.append(literal.value());
                continue;
            }
            if (segment instanceof CompiledTemplate.TokenSegment token) {
                String resolved = placeholderService.resolve(token.token(), context).orElse('%' + token.token() + '%');
                output.append(resolved);
            }
        }
        return output.toString();
    }
}

