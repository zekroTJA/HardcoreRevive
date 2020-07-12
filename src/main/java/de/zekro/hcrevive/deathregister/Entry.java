package de.zekro.hcrevive.deathregister;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * {@link DeathRegister} entry element wrapping the
 * died {@link Player}, the death {@link Location},
 * the {@link World} the player died in and an
 * optional {@link Runnable} which is called when
 * the entry was removed or has expired.
 */
public class Entry {
    private Player player;
    private Location location;
    private World world;
    private Runnable removeCallback;

    /**
     * Initialize new {@link Entry} with passed
     * death victim.
     * @param player death victim
     */
    public Entry(Player player) {
        this(player, null);
    }

    /**
     * Initialize new {@link Entry} with passed
     * death victim and remove callback {@link Runnable}.
     * @param player death victim
     * @param removeCallback remove callback runnable
     */
    public Entry(Player player, Runnable removeCallback) {
        this.player = player;
        this.removeCallback = removeCallback;
        this.world = player.getWorld();
        this.location = player.getLocation();
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return this.world;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Calls the passed remove/expire callback
     * when defined.
     */
    public void runRemoveCallback() {
        if (this.removeCallback != null)
            this.removeCallback.run();
    }
 }
