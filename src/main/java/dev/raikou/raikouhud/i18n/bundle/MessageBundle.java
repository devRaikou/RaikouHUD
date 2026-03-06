package dev.raikou.raikouhud.i18n.bundle;

import java.util.Map;

public record MessageBundle(
    String localeCode,
    Map<String, String> messages
) {
    public String message(String key) {
        return messages.get(key);
    }
}

