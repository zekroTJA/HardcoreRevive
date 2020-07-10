package de.zekro.hcrevive.deathregister;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Entry {
    private Player player;
    private Location location;
    private World world;
    private long expires;

    public Entry(Player player, long expires) {
        this.player = player;
        this.world = player.getWorld();
        this.location = player.getLocation();
        this.expires = expires;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isExpired() {
        return this.expires > 0 && this.player.getWorld().getFullTime() > this.expires;
    }
}
