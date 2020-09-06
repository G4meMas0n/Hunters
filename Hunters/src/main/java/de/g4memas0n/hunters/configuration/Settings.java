package de.g4memas0n.hunters.configuration;

import de.g4memas0n.hunters.Hunters;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

/**
 * The Settings class that represent the configuration file of this plugin.
 *
 * @author G4meMason
 * @since Release 1.0.0
 */
public final class Settings {

    private static final String FILE_CONFIG = "config.yml";
    private static final String FILE_CONFIG_BROKEN = "config.broken.yml";

    private final Hunters instance;
    private final YamlStorageFile storage;

    // Notification-Settings:
    private boolean automatic;
    private boolean everyone;
    //private boolean title;
    private int warmup;

    // Tracking-Settings:
    private boolean notify;
    private int timer;

    public Settings(@NotNull final Hunters instance) {
        this.instance = instance;
        this.storage = new YamlStorageFile(new File(instance.getDataFolder(), FILE_CONFIG));
    }

    @SuppressWarnings("unused")
    public void delete() {
        try {
            this.storage.delete();

            this.instance.getLogger().debug("Deleted configuration file: " + this.storage.getFile().getName());
        } catch (IOException ex) {
            this.instance.getLogger().warning(String.format("Unable to delete configuration file '%s': %s",
                    this.storage.getFile().getName(), ex.getMessage()));
        }
    }

    public void load() {
        try {
            this.storage.load();

            this.instance.getLogger().debug("Loaded configuration file: " + this.storage.getFile().getName());
        } catch (FileNotFoundException ex) {
            this.instance.getLogger().warning(String.format("Unable to find configuration file '%s'. "
                    + "Saving default configuration...", this.storage.getFile().getName()));

            this.instance.saveResource(FILE_CONFIG, true);
            this.instance.getLogger().info(String.format("Saved default configuration from template: %s", FILE_CONFIG));
            this.load();
        } catch (InvalidConfigurationException ex) {
            this.instance.getLogger().severe(String.format("Unable to load configuration file '%s', because it is broken. "
                    + "Renaming it and saving default configuration...", this.storage.getFile().getName()));

            final File broken = new File(this.instance.getDataFolder(), FILE_CONFIG_BROKEN);

            if (broken.exists() && broken.delete()) {
                this.instance.getLogger().debug("Deleted old broken configuration file: " + broken.getName());
            }

            if (this.storage.getFile().renameTo(broken)) {
                this.instance.getLogger().info(String.format("Renamed broken configuration file '%s' to: %s",
                        this.storage.getFile().getName(), broken.getName()));
            }

            this.instance.saveResource(FILE_CONFIG, true);
            this.instance.getLogger().info(String.format("Saved default configuration from template: %s", FILE_CONFIG));
            this.load();
        } catch (IOException ex) {
            this.instance.getLogger().warning(String.format("Unable to load configuration file '%s'. "
                    + "Loading default configuration...", this.storage.getFile().getName()));

            this.storage.clear();
        }

        this.notify = this._getNotifyEnabled();
        this.everyone = this._getNotifyEveryone();
        //this.title = this._getNotifyTitle();
        this.warmup = this._getNotifyWarmup();

        this.automatic = this._getTrackAutomatic();
        this.timer = this._getTrackTimer();
    }

    @SuppressWarnings("unused")
    public void save() {
        /*
        Disabled, because it is not intended to save the config file, as this breaks the comments.
        try {
            this.storage.save();
            this.instance.getLogger().debug("Saved configuration file: " + this.storage.getFile().getName());
        } catch (IOException ex) {
            this.instance.getLogger().warning(String.format("Unable to save configuration file '%s': %s",
                    this.storage.getFile().getName(), ex.getMessage()));
        }
         */
    }

    // Plugin-Settings Methods:
    protected boolean _getDebug() {
        return this.storage.getBoolean("debug", false);
    }

    public boolean isDebug() {
        return this._getDebug();
    }

    protected @NotNull Locale _getLocale() {
        final Locale locale = this.storage.getLocale("locale");

        if (locale == null) {
            return Locale.ENGLISH;
        }

        return locale;
    }

    public @NotNull Locale getLocale() {
        return this._getLocale();
    }

    // Notification-Settings Methods:
    protected boolean _getNotifyEnabled() {
        return this.storage.getBoolean("notify.enabled", true);
    }

    public boolean isNotifyEnabled() {
        return this.notify;
    }

    protected boolean _getNotifyEveryone() {
        return this.storage.getBoolean("notify.everyone", true);
    }

    public boolean isNotifyEveryone() {
        return this.everyone;
    }

    /*
    protected boolean _getNotifyTitle() {
        return this.storage.getBoolean("notify.title", false);
    }

    public boolean isNotifyTitle() {
        return this.title;
    }
     */

    protected int _getNotifyWarmup() {
        final int warmup = this.storage.getInt("notify.warmup", 3);

        if (warmup < 0 || warmup > 10) {
            this.instance.getLogger().warning(String.format("Detected invalid notify warmup in configuration file '%s': "
                    + "Warmup is out of range.", this.storage.getFile().getName()));

            return 3;
        }

        return warmup;
    }

    public int getNotifyWarmup() {
        return this.warmup;
    }

    public boolean isNotifyWarmup() {
        return this.warmup > 0;
    }

    // Tracking-Settings Methods:
    protected boolean _getTrackAutomatic() {
        return this.storage.getBoolean("track.automatic", true);
    }

    public boolean isTrackAutomatic() {
        return this.automatic;
    }

    protected int _getTrackTimer() {
        final int timer = this.storage.getInt("track.timer", 60);

        if (timer < 0) {
            this.instance.getLogger().warning(String.format("Detected invalid track timer in configuration file '%s': "
                    + "Timer is out of range.", this.storage.getFile().getName()));

            return 60;
        }

        if (timer == 0 && this._getTrackAutomatic()) {
            this.instance.getLogger().warning(String.format("Detected invalid track timer in configuration file '%s': "
                    + "Timer can not be zero when automatic tracking is enabled.", this.storage.getFile().getName()));

            return 60;
        }

        return timer;
    }

    public int getTrackTimer() {
        return this.timer;
    }

    public boolean isTrackTimer() {
        return this.timer > 0;
    }
}
