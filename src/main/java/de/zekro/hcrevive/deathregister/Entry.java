package de.zekro.hcrevive.deathregister;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

// TODO: Docs

public class Entry {
    private Player player;
    private Location location;
    private World world;
    private Runnable removeCallback;

    public Entry(Player player) {
        this(player, null);
    }

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

    public void runRemoveCallback() {
        if (this.removeCallback != null)
            this.removeCallback.run();
    }
 }
