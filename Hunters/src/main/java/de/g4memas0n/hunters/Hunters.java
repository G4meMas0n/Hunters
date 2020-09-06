package de.g4memas0n.hunters;

import de.g4memas0n.hunters.util.logging.BasicLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public final class Hunters extends JavaPlugin {

    private final BasicLogger logger;

    private boolean loaded;
    private boolean enabled;

    public Hunters() {
        this.logger = new BasicLogger(super.getLogger(), "Plugin", this.getName());
    }

    @Override
    public @NotNull BasicLogger getLogger() {
        return this.logger;
    }

    @Override
    public void onLoad() {
        if (this.loaded) {
            this.getLogger().severe("Tried to load plugin twice. Plugin is already loaded.");
            return;
        }

        super.onLoad();

        this.loaded = true;
    }

    @Override
    public void onEnable() {
        if (this.enabled) {
            this.getLogger().severe("Tried to enable plugin twice. Plugin is already enabled.");
            return;
        }

        if (!this.loaded) {
            this.getLogger().warning("Plugin was not loaded. Loading it...");
            this.onLoad();
        }

        super.onEnable();

        this.enabled = true;
    }

    @Override
    public void onDisable() {
        if (!this.enabled) {
            this.getLogger().severe("Tried to disable plugin twice. Plugin is already disabled.");
            return;
        }

        super.onDisable();

        this.enabled = false;
        this.loaded = false;
    }

    public @NotNull BukkitTask runTask(@NotNull final Runnable task) {
        return this.getServer().getScheduler().runTask(this, task);
    }

    public @NotNull BukkitTask scheduleTask(@NotNull final Runnable task, final long delay) {
        return this.getServer().getScheduler().runTaskLater(this, task, delay);
    }
}
