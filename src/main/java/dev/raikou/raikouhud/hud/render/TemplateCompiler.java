package dev.raikou.raikouhud.hud.render;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TemplateCompiler {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("%([A-Za-z0-9_]+)%");

    public CompiledTemplate compile(String template) {
        String source = template == null ? "" : template;
        Matcher matcher = TOKEN_PATTERN.matcher(source);
        List<CompiledTemplate.Segment> segments = new ArrayList<>();
        int index = 0;

        while (matcher.find()) {
            if (matcher.start() > index) {
                segments.add(new CompiledTemplate.LiteralSegment(source.substring(index, matcher.start())));
            }
            segments.add(new CompiledTemplate.TokenSegment(matcher.group(1)));
            index = matcher.end();
        }

        if (index < source.length()) {
            segments.add(new CompiledTemplate.LiteralSegment(source.substring(index)));
        }

        return new CompiledTemplate(List.copyOf(segments));
    }
}

