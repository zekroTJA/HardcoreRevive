package de.zekro.hcrevive;

import de.zekro.hcrevive.deathregister.DeathRegister;
import de.zekro.hcrevive.listeners.DeathListener;
import de.zekro.hcrevive.listeners.SneakListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public final class HardcoreRevive extends JavaPlugin {

    private DeathRegister deathRegister;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (!this.getConfig().getBoolean("enable", true)) {
            this.getLogger().log(Level.WARNING, "disabled by config");
            return;
        }

        if (!this.getServer().isHardcore()) {
            this.getLogger().log(Level.WARNING, "disabled when server is not in hardcore mode");
            return;
        }

        this.deathRegister = new DeathRegister(this);

        this.registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new DeathListener(this, this.deathRegister, this.getLogger()), this);
        pm.registerEvents(new SneakListener(this, this.deathRegister), this);
    }
}
