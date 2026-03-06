package dev.raikou.raikouhud.hud.placeholder;

import dev.raikou.raikouhud.hud.render.RenderContext;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class BuiltinPlaceholderResolver implements PlaceholderResolver {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public Optional<String> resolve(String token, RenderContext context) {
        String key = token.toLowerCase(Locale.ROOT);
        Player player = context.player();

        return switch (key) {
            case "player_name" -> Optional.of(player.getName());
            case "player_display_name" -> Optional.of(player.getDisplayName());
            case "player_ping" -> Optional.of(Integer.toString(player.getPing()));
            case "player_world" -> Optional.of(player.getWorld().getName());
            case "player_x" -> Optional.of(Integer.toString(player.getLocation().getBlockX()));
            case "player_y" -> Optional.of(Integer.toString(player.getLocation().getBlockY()));
            case "player_z" -> Optional.of(Integer.toString(player.getLocation().getBlockZ()));
            case "player_health" -> Optional.of(String.format(Locale.US, "%.1f", player.getHealth()));
            case "player_health_ratio" -> Optional.of(String.format(Locale.US, "%.2f", healthRatio(player)));
            case "server_online" -> Optional.of(Integer.toString(Bukkit.getOnlinePlayers().size()));
            case "server_max_players" -> Optional.of(Integer.toString(Bukkit.getMaxPlayers()));
            case "server_tps_1m" -> Optional.of(readOneMinuteTps());
            case "time_hhmmss" -> Optional.of(LocalTime.now().format(TIME_FORMAT));
            default -> Optional.empty();
        };
    }

    private double healthRatio(Player player) {
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHealth = maxHealthAttribute == null ? 20.0D : maxHealthAttribute.getValue();
        if (maxHealth <= 0.0D) {
            return 0.0D;
        }
        return Math.max(0.0D, Math.min(1.0D, player.getHealth() / maxHealth));
    }

    private String readOneMinuteTps() {
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getTPS");
            Object tps = method.invoke(Bukkit.getServer());
            if (tps instanceof double[] values && values.length > 0) {
                return String.format(Locale.US, "%.2f", values[0]);
            }
        } catch (ReflectiveOperationException ignored) {
            return "N/A";
        }
        return "N/A";
    }
}
