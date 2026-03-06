package dev.raikou.raikouhud.i18n;

import dev.raikou.raikouhud.util.MiniMessageSupport;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.bukkit.command.CommandSender;

public final class MessageService {

    private final LocaleService localeService;

    public MessageService(LocaleService localeService) {
        this.localeService = Objects.requireNonNull(localeService, "localeService");
    }

    public String render(MessageKey key) {
        return Objects.requireNonNullElse(localeService.message(key.path()), "");
    }

    public String render(MessageKey key, Map<String, String> placeholders) {
        String template = localeService.message(key.path());
        return applyPlaceholders(template, placeholders);
    }

    public void send(CommandSender sender, MessageKey key) {
        send(sender, key, Collections.emptyMap());
    }

    public void send(CommandSender sender, MessageKey key, Map<String, String> placeholders) {
        String full = render(MessageKey.PREFIX) + render(key, placeholders);
        sender.sendMessage(MiniMessageSupport.toLegacySection(full));
    }

    public void sendRaw(CommandSender sender, MessageKey key) {
        sender.sendMessage(MiniMessageSupport.toLegacySection(render(key)));
    }

    public void sendRaw(CommandSender sender, MessageKey key, Map<String, String> placeholders) {
        sender.sendMessage(MiniMessageSupport.toLegacySection(render(key, placeholders)));
    }

    public String activeLocaleCode() {
        return localeService.activeLocaleCode();
    }

    private String applyPlaceholders(String message, Map<String, String> placeholders) {
        String output = Objects.requireNonNullElse(message, "");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            output = output.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return output;
    }
}
