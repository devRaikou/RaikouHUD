package dev.raikou.raikouhud.hud.update;

import dev.raikou.raikouhud.hud.HudModule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class UpdateCoordinator {

    private final JavaPlugin plugin;
    private final Map<Integer, List<HudModule>> modulesByInterval;
    private final Map<Integer, BukkitTask> tasksByInterval;

    public UpdateCoordinator(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.modulesByInterval = new HashMap<>();
        this.tasksByInterval = new HashMap<>();
    }

    public void rebuild(Collection<? extends HudModule> modules) {
        stop();
        modulesByInterval.clear();
        for (HudModule module : modules) {
            if (!module.isGloballyEnabled()) {
                continue;
            }
            modulesByInterval.computeIfAbsent(module.updateIntervalTicks(), ignored -> new ArrayList<>()).add(module);
        }
        start();
    }

    public List<UpdateBucket> buckets() {
        return modulesByInterval.entrySet().stream()
            .map(entry -> new UpdateBucket(entry.getKey(), List.copyOf(entry.getValue())))
            .collect(Collectors.toList());
    }

    public void stop() {
        for (BukkitTask task : tasksByInterval.values()) {
            task.cancel();
        }
        tasksByInterval.clear();
    }

    private void start() {
        for (Map.Entry<Integer, List<HudModule>> entry : modulesByInterval.entrySet()) {
            int interval = entry.getKey();
            List<HudModule> modules = entry.getValue();
            BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                () -> modules.forEach(module -> module.tick(UpdateReason.SCHEDULED)),
                interval,
                interval
            );
            tasksByInterval.put(interval, task);
        }
    }
}

