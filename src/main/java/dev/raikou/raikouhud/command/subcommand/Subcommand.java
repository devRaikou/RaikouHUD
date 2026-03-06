package dev.raikou.raikouhud.command.subcommand;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface Subcommand {

    String name();

    String permission();

    default boolean playerOnly() {
        return false;
    }

    void execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}

