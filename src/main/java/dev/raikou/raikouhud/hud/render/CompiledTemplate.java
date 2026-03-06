package dev.raikou.raikouhud.hud.render;

import java.util.List;

public record CompiledTemplate(List<Segment> segments) {

    public sealed interface Segment permits LiteralSegment, TokenSegment {
    }

    public record LiteralSegment(String value) implements Segment {
    }

    public record TokenSegment(String token) implements Segment {
    }
}

