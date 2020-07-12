package de.zekro.hcrevive.deathregister;

import de.zekro.hcrevive.HardcoreRevive;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The DeathRegister holds record of all occurred deaths
 * which can be revived by another player.
 */
public class DeathRegister {

    private HardcoreRevive pluginInstance;
    private ArrayList<Entry> register;

    /**
     * Initializes new instance of {@link DeathRegister}.
     * @param pluginInstance plugin instance
     */
    public DeathRegister(HardcoreRevive pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.register = new ArrayList<>();
    }

    /**
     * Register a new death.
     * When expiresIn is '0', the entry will never expire.
     * @param player victim player
     * @param expiresIn expiration in game ticks
     */
    public void register(Player player, long expiresIn) {
        this.register(player, expiresIn, null);
    }

    /**
     * Register a new death.
     * When expiresIn is '0', the entry will never expire.
     * @param player victim player
     * @param expiresIn expiration in game ticks
     * @param removeCallback remove/expiration callback
     */
    public void register(Player player, long expiresIn, Runnable removeCallback) {
        Entry entry = new Entry(player, removeCallback);
        this.register.add(entry);

        if (expiresIn > 0) {
            this.pluginInstance.getServer().getScheduler()
                    .runTaskLater(this.pluginInstance, () -> this.remove(entry), expiresIn);
        }
    }

    /**
     * Returns a list of entries where the given location is
     * in the sphericalRadius of the death entry location.
     * @param location current reviver's location
     * @param sphericalRadius valid spherical radius around the death
     * @return list of revivable death entries
     */
    public List<Entry> get(Location location, double sphericalRadius) {
        return this.register.stream()
                .filter(e -> this.isInRange(e.getLocation(), location, sphericalRadius))
                .collect(Collectors.toList());
    }

    /**
     * Remove the given entry from the register by
     * given entry object.
     * @param entry entry to remove
     */
    public void remove(Entry entry) {
        this.register.remove(entry);
        entry.runRemoveCallback();
    }

    /**
     * Removes an entry from the register by the
     * given player object.
     * @param player death victim player object
     */
    public void remove(Player player) {
        this.register.stream()
                .filter(e -> e.getPlayer() == player)
                .findFirst().ifPresent(this::remove);
    }

    /**
     * Flushes the whole death register.
     * This executes each entries remove callback.
     */
    public void flush() {
        this.register.forEach(Entry::runRemoveCallback);
        this.register.clear();
    }

    /**
     * Returns true when the distance between loc1 and
     * loc2 is equal or smaller than sphericalRadius.
     * @param loc1 location 1
     * @param loc2 location 2
     * @param sphericalRadius valid spherical radius
     * @return whether the distance is smaller or equal sphericalRadius
     */
    private boolean isInRange(Location loc1, Location loc2, double sphericalRadius) {
        return loc1.distance(loc2) <= sphericalRadius;
    }
}
