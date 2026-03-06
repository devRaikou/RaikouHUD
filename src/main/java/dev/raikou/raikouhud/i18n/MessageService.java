package dev.raikou.raikouhud.i18n;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public final class MessageService {

    private final LocaleService localeService;

    public MessageService(LocaleService localeService) {
        this.localeService = Objects.requireNonNull(localeService, "localeService");
    }

    public String render(MessageKey key) {
        return colorize(localeService.message(key.path()));
    }

    public String render(MessageKey key, Map<String, String> placeholders) {
        String template = localeService.message(key.path());
        return colorize(applyPlaceholders(template, placeholders));
    }

    public void send(CommandSender sender, MessageKey key) {
        send(sender, key, Collections.emptyMap());
    }

    public void send(CommandSender sender, MessageKey key, Map<String, String> placeholders) {
        String prefix = render(MessageKey.PREFIX);
        String message = render(key, placeholders);
        sender.sendMessage(prefix + message);
    }

    public void sendRaw(CommandSender sender, MessageKey key) {
        sender.sendMessage(render(key));
    }

    public void sendRaw(CommandSender sender, MessageKey key, Map<String, String> placeholders) {
        sender.sendMessage(render(key, placeholders));
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

    private String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNullElse(input, ""));
    }
}

