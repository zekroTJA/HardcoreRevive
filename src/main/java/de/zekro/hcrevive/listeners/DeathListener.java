package de.zekro.hcrevive.listeners;

import de.zekro.hcrevive.HardcoreRevive;
import de.zekro.hcrevive.deathregister.DeathRegister;
import de.zekro.hcrevive.util.TimeUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Logger;
import java.util.logging.Level;

public class DeathListener implements Listener {

    private final HardcoreRevive pluginInstance;
    private final DeathRegister deathRegister;
    private final Logger logger;
    private final int reviveTimeout;

    public DeathListener(HardcoreRevive pluginInstance, DeathRegister deathRegister, Logger logger) {
        this.pluginInstance = pluginInstance;
        this.deathRegister = deathRegister;
        this.logger = logger;

        this.reviveTimeout = this.pluginInstance.getConfig().getInt("reviveTimeout", 0);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World world = player.getWorld();

        if (world.getPlayers().size() < 2) { // TODO: Re-enable
            player.sendMessage("Sorry, you are the only player on the server, so you can not be revived. :(");
            return;
        }

        Location deathLocation = player.getLocation().clone();
        BukkitTask particleTask = this.pluginInstance.getServer().getScheduler().runTaskTimer(this.pluginInstance, () -> {
            world.spawnParticle(Particle.CLOUD, deathLocation, 10);
        }, 0, 5);

        this.deathRegister.register(player, reviveTimeout * 20, particleTask::cancel);

        world.getPlayers().stream()
                .filter(p -> p != player)
                .forEach(p -> p.sendMessage(this.getDeathBroadcastMessage(player)));

        player.sendMessage(this.getDeathVictimMessage());

        this.logger.log(Level.INFO, String.format("Player %s died in world %s", player.getName(), world.getName()));
    }

    private String getDeathBroadcastMessage(Player player) {
        StringBuilder res = new StringBuilder();
        Location loc = player.getLocation();

        res.append(String.format("%s died at %d/%d/%d!",
                player.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

        if (this.reviveTimeout > 0) {
            res.append(String.format(" You have %s to reach this location.",
                    TimeUtil.getFormattedTimeSpan(this.reviveTimeout)));
        }

        res.append(" Go and revive them!");

        return res.toString();
    }

    private String getDeathVictimMessage() {
        StringBuilder res = new StringBuilder();

        res.append("Hey, you're good? You can be revived by another player on the server!");

        if (this.reviveTimeout > 0) {
            res.append(String.format(" But they have only %s to reach your location to revive you.",
                    TimeUtil.getFormattedTimeSpan(this.reviveTimeout)));
        }

        return res.toString();
    }
}
