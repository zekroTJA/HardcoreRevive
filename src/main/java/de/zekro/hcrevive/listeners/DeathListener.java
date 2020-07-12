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

/**
 * Listener class binding the {@link PlayerDeathEvent}.
 *
 * When a player dies and another player is on the server,
 * the death is registered with the death location to be
 * revived by another player.
 */
public class DeathListener implements Listener {

    private final HardcoreRevive pluginInstance;
    private final DeathRegister deathRegister;
    private final Logger logger;
    private final int reviveTimeout;

    /**
     * Initializes an ew instance of {@link DeathListener}.
     * @param pluginInstance The plugin instance
     * @param deathRegister The death register instance.
     * @param logger The logger instance.
     */
    public DeathListener(HardcoreRevive pluginInstance, DeathRegister deathRegister, Logger logger) {
        this.pluginInstance = pluginInstance;
        this.deathRegister = deathRegister;
        this.logger = logger;

        this.reviveTimeout = this.pluginInstance.getConfig().getInt("reviveTimeout", 0);
    }

    /**
     * {@link PlayerDeathEvent} event listener.
     * @param event player death event
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        World world = player.getWorld();

        // Don't continue when less than two players are on the server.
        // TODO: Make this configurable.
        if (world.getPlayers().size() < 2) {
            player.sendMessage("Sorry, you are the only player on the server, so you can not be revived. :(");
            return;
        }

        Location deathLocation = player.getLocation(); // Returns a deep copy of the players current location.
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

    /**
     * Returns a message sent to all other players on the server to indicate
     * where the player died to be revived.
     * If a timeout is specified, this will be given as well in the message.
     * @param victim dead player
     * @return formatted message
     */
    private String getDeathBroadcastMessage(Player victim) {
        StringBuilder res = new StringBuilder();
        Location loc = victim.getLocation();

        res.append(String.format("%s died at %d/%d/%d!",
                victim.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

        if (this.reviveTimeout > 0) {
            res.append(String.format(" You have %s to reach this location.",
                    TimeUtil.getFormattedTimeSpan(this.reviveTimeout)));
        }

        res.append(" Go and revive them!");

        return res.toString();
    }

    /**
     * Message which is sent to the player died.
     * If a timeout is specified, the time until that will be
     * displayed as well.
     * @return Formatted message.
     */
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
