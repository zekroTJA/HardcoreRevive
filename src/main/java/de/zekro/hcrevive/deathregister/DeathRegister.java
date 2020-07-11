package de.zekro.hcrevive.deathregister;

import de.zekro.hcrevive.HardcoreRevive;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeathRegister {

    private HardcoreRevive pluginInstance;
    private ArrayList<Entry> register;

    public DeathRegister(HardcoreRevive pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.register = new ArrayList<>();
    }

    public void register(Player player, long expiresIn) {
        this.register(player, expiresIn, null);
    }

    public void register(Player player, long expiresIn, Runnable removeCallback) {
        Entry entry = new Entry(player, removeCallback);
        this.register.add(entry);

        if (expiresIn > 0) {
            this.pluginInstance.getServer().getScheduler()
                    .runTaskLater(this.pluginInstance, () -> this.remove(entry), expiresIn);
        }
    }

    public List<Entry> get(Location location, double sphericalRadius) {
        return this.register.stream()
                .filter(e -> this.isInRange(e.getLocation(), location, sphericalRadius))
                .collect(Collectors.toList());
    }

    public void remove(Entry entry) {
        this.register.remove(entry);
        entry.runRemoveCallback();
    }

    private boolean isInRange(Location loc1, Location loc2, double sphericalRadius) {
        return loc1.distance(loc2) <= sphericalRadius;
    }
}
