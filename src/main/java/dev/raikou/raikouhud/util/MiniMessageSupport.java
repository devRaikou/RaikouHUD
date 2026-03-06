package dev.raikou.raikouhud.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class MiniMessageSupport {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    private MiniMessageSupport() {
    }

    public static String toLegacySection(String miniMessageText) {
        String input = miniMessageText == null ? "" : miniMessageText;
        try {
            return LEGACY_SERIALIZER.serialize(MINI_MESSAGE.deserialize(input));
        } catch (Exception ignored) {
            return input;
        }
    }
}

