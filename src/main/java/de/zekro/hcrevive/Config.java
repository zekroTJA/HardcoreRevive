package de.zekro.hcrevive;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;

public class Config extends JavaPlugin {

    private FileConfiguration config;
    private File configFile;

    public void reloadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(getDataFolder(), "config.yml");
        }

        this.config = YamlConfiguration.loadConfiguration(this.configFile);

        Reader defConfigStream = new InputStreamReader(this.getResource("config.yml"));
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        this.config.setDefaults(defConfig);
    }

    public void saveConfig() {
        if (this.config == null || configFile == null)
            return;

        try {
            this.getConfig().save(configFile);
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Saving config failed: ", ex);
        }
    }
}
