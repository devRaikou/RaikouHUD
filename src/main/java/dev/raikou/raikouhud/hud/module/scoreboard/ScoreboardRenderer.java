package dev.raikou.raikouhud.hud.module.scoreboard;

import dev.raikou.raikouhud.config.model.module.ScoreboardTakeoverMode;
import dev.raikou.raikouhud.player.PlayerSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public final class ScoreboardRenderer {

    private static final String OBJECTIVE_NAME = "raikouhud";
    private static final String META_BOARD = "scoreboard.board";
    private static final String META_HASH = "scoreboard.hash";
    private static final String META_ENTRIES = "scoreboard.entries";

    public boolean render(
        Player player,
        PlayerSession session,
        String title,
        List<String> lines,
        ScoreboardTakeoverMode takeoverMode
    ) {
        String hash = title + "||" + String.join("\n", lines);
        Object previousHash = session.metadata(META_HASH);
        if (hash.equals(previousHash)) {
            return false;
        }

        Scoreboard board = resolveBoard(player, session, takeoverMode);
        if (board == null) {
            return false;
        }

        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            objective = board.registerNewObjective(OBJECTIVE_NAME, "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        objective.setDisplayName(trim(colorize(title), 32));

        @SuppressWarnings("unchecked")
        List<String> oldEntries = (List<String>) session.metadata(META_ENTRIES);
        if (oldEntries != null) {
            for (String old : oldEntries) {
                board.resetScores(old);
            }
        }

        List<String> newEntries = prepareLines(lines);
        int score = newEntries.size();
        for (String line : newEntries) {
            objective.getScore(line).setScore(score--);
        }

        session.putMetadata(META_HASH, hash);
        session.putMetadata(META_ENTRIES, List.copyOf(newEntries));
        return true;
    }

    public void clear(Player player, PlayerSession session) {
        Object metaBoard = session.removeMetadata(META_BOARD);
        session.removeMetadata(META_HASH);
        session.removeMetadata(META_ENTRIES);
        if (!(metaBoard instanceof Scoreboard board)) {
            return;
        }
        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective != null) {
            objective.unregister();
        }
        if (player.getScoreboard().equals(board)) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager != null) {
                player.setScoreboard(manager.getMainScoreboard());
            }
        }
    }

    private Scoreboard resolveBoard(Player player, PlayerSession session, ScoreboardTakeoverMode takeoverMode) {
        Object existing = session.metadata(META_BOARD);
        Scoreboard ourBoard = existing instanceof Scoreboard board ? board : null;

        Scoreboard current = player.getScoreboard();
        Objective currentSidebar = current.getObjective(DisplaySlot.SIDEBAR);

        if (takeoverMode == ScoreboardTakeoverMode.SOFT
            && currentSidebar != null
            && !OBJECTIVE_NAME.equals(currentSidebar.getName())
            && (ourBoard == null || !current.equals(ourBoard))) {
            return null;
        }

        if (ourBoard == null) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager == null) {
                return null;
            }
            ourBoard = manager.getNewScoreboard();
            session.putMetadata(META_BOARD, ourBoard);
        }

        if (!player.getScoreboard().equals(ourBoard)) {
            player.setScoreboard(ourBoard);
        }

        return ourBoard;
    }

    private List<String> prepareLines(List<String> lines) {
        int maxLines = Math.min(15, lines.size());
        List<String> output = new ArrayList<>(maxLines);
        Set<String> used = new HashSet<>();

        for (int index = 0; index < maxLines; index++) {
            String line = trim(colorize(lines.get(index)), 40);
            String unique = line;
            while (used.contains(unique)) {
                unique = trim(unique + ChatColor.RESET, 40);
            }
            used.add(unique);
            output.add(unique);
        }
        return output;
    }

    private String colorize(String value) {
        return ChatColor.translateAlternateColorCodes('&', value == null ? "" : value);
    }

    private String trim(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }
}
